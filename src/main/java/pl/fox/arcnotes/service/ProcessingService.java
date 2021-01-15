package pl.fox.arcnotes.service;

import com.google.cloud.automl.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.fox.arcnotes.model.Note;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class ProcessingService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingService.class);

    private static final String PROJECT_ID = "150146461045";             //google project id
    private static final String LOCATION = "us-central1";                //google project location
    private static final String VISION_MODEL = "IOD5055336761211748352"; //google taught vision api serial
    private static final double SCORE_THRESHOLD = 0.6;      //Border value of results score (getAll > SCORE_THRESHOLD)

    private static final String FILE_EXT = "wav";
    private static final AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;

    private final ResourceLoader loader;

    @Autowired
    public ProcessingService(ResourceLoader loader) {
        this.loader = loader;
    }


    /*
    *
    * 2021-01-14 18:04:22.473 ERROR 12204 --- [   NoteThread-1] i.g.i.ManagedChannelOrphanWrapper
    *  : *~*~*~ Channel ManagedChannelImpl{logId=3, target=automl.googleapis.com:443} was not shutdown properly!!! ~*~*~*
    Make sure to call shutdown()/shutdownNow() and wait until awaitTermination() returns true.
    * java.lang.RuntimeException: ManagedChannel allocation site
    *
    * */
    //@TODO: Got this multithreading error, solve it later!


    /*
     io.grpc.StatusRuntimeException: PERMISSION_DENIED:
     Your application has authenticated using end user credentials from the Google Cloud SDK or Google Cloud Shell
     which are not supported by the automl.googleapis.com. We recommend configuring the billing/quota_project setting
     in gcloud or using a service account through the auth/impersonate_service_account setting.
     For more information about service accounts and how to use them in your application, see https://cloud.google.com/docs/authentication/.
     @TODO: Got this error on uploading one more file when Async is off <- fix it and take care of the upper one!
     */


    /*
    c.g.a.oauth2.DefaultCredentialsProvider  : Your application has authenticated using end user credentials from Google Cloud SDK.
    We recommend that most server applications use service accounts instead. If your application continues to use end user credentials from Cloud SDK,
    you might receive a "quota exceeded" or "API not enabled" error. For more information about service accounts, see https://cloud.google.com/docs/authentication/.
    o.s.c.g.core.DefaultCredentialsProvider  : Default credentials provider for user 764086051850-6qr4p6gpi6hn506pt8ejuq83di341hur.apps.googleusercontent.com
     Scopes in use by default credentials: [https://www.googleapis.com/auth/pubsub, https://www.googleapis.com/auth/spanner.admin, https://www.googleapis.com/auth/spanner.data,
     https://www.googleapis.com/auth/datastore, https://www.googleapis.com/auth/sqlservice.admin,
     https://www.googleapis.com/auth/devstorage.read_only, https://www.googleapis.com/auth/devstorage.read_write,
      https://www.googleapis.com/auth/cloudruntimeconfig, https://www.googleapis.com/auth/trace.append,
       https://www.googleapis.com/auth/cloud-platform, https://www.googleapis.com/auth/cloud-vision,
       https://www.googleapis.com/auth/bigquery, https://www.googleapis.com/auth/monitoring.write]
       @TODO: Resolve this problem, that is connected to the upper case
     :*/


    @Async
    public CompletableFuture<MultipartFile> process(MultipartFile file) throws IOException, UnsupportedAudioFileException {
        java.util.List<Note> notes = new java.util.ArrayList<>();
        PredictResponse response = buildResponse(file);
        StringBuilder sb = new StringBuilder();

        response.getPayloadList().forEach(pl -> {
            sb.append(pl.getDisplayName()).append(" ");
            notes.add(new Note(pl.getDisplayName(), pl.getClassification().getScore()));
        });

        LOG.info("Size: {}, Notes: {}", notes.size(), sb.toString());

        return CompletableFuture.completedFuture(new MockMultipartFile("Henlo", new FileInputStream(Objects.requireNonNull(mergeNotes(getClips(notes))))));
    }

    private PredictResponse buildResponse(MultipartFile file) throws IOException {
//        Image img = Image.newBuilder().setImageBytes(ByteString.copyFrom(
//                Files.readAllBytes(loader.getResource("classpath:/maxresdefault.jpg").getFile().toPath()))).build();

//       Image img = Image.newBuilder().setImageBytes(ByteString.copyFrom(file.getBytes())).build();

        return PredictionServiceClient.create().predict(
                PredictRequest.newBuilder()
                        .putParams("score_threshold", String.valueOf(SCORE_THRESHOLD))
                        .setPayload(ExamplePayload.newBuilder().setImage(Image.newBuilder().setImageBytes(ByteString.copyFrom(file.getBytes())).build()).build())
                        .setName(ModelName.of(PROJECT_ID, LOCATION, VISION_MODEL).toString())
                        .build());
    }


    /*
    * @TODO:
        File file = new File(filePath);
        AudioInputStream clip = AudioSystem.getAudioInputStream(file); //THROWS EXCEPTION
        List<File> file = new ArrayList<>();
        List<AudioInputStream> clips = new ArrayList<>();
        file.forEach(e -> {clips.add(AudioSystem.getAudioInputStream(e))});
    * */

    private File mergeNotes(java.util.List<AudioInputStream> clips) throws IOException {
        if (clips.size() < 2) {
            LOG.error("Too few notes read");
            return null;
        }

        String res = java.util.UUID.randomUUID().toString().concat(FILE_EXT);
        AudioInputStream files = null;

        for (int i = 0; i < clips.size() - 1; i++) {
            if (i == 0) {
                files = new AudioInputStream(
                        new SequenceInputStream(clips.get(i), clips.get(i + 1)), clips.get(i).getFormat(),
                        clips.get(i).getFrameLength() + clips.get(i + 1).getFrameLength());
                continue;
            }
            files = new AudioInputStream(
                    new SequenceInputStream(files, clips.get(i + 1)), files.getFormat(),
                    files.getFrameLength() + clips.get(i + 1).getFrameLength());
        }

        File f = new File(".\\"+res+"."+FILE_EXT);

        assert files != null;
        AudioSystem.write(files, FILE_TYPE, f);

        LOG.info("Created new File: {}", f.getAbsolutePath());

        return f;
    }

    private java.util.List<AudioInputStream> getClips(java.util.List<Note> notes) {
        java.util.List<AudioInputStream> musicFiles = new java.util.ArrayList<>();                  // init AudioInputStream list
        java.util.Map<String, Note> noteMap = new java.util.HashMap<>();                            // init HashMap

        notes.stream().filter(n -> !noteMap.containsKey(n.getType())).forEach(n -> noteMap.put(n.getType(), n));    // Add notes to hashmap by key

        try{
            for (String key : noteMap.keySet()) {
                LOG.info("{}", noteMap.keySet());

                File file = ResourceUtils.getFile("classpath:" + key + "." + FILE_EXT);

                musicFiles.add(AudioSystem.getAudioInputStream(file)); //TODO: javax.sound.sampled.UnsupportedAudioFileException: File of unsupported format
            }
        }catch(IOException | UnsupportedAudioFileException ie){
            ie.printStackTrace();
        }


        LOG.info("Loaded {} music files", musicFiles.size());

        return musicFiles;
    }
}
