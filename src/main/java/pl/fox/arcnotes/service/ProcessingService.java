package pl.fox.arcnotes.service;

import com.google.cloud.automl.v1.*;
import com.google.common.net.MediaType;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class ProcessingService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingService.class);

    private static final String PROJECT_ID = "150146461045";             //google project id
    private static final String LOCATION = "us-central1";                //google project location
    private static final String VISION_MODEL = "IOD5055336761211748352"; //google taught vision api serial
    private static final double SCORE_THRESHOLD = 0.6;      //Border value of results score (getAll > SCORE_THRESHOLD)

    private static final String FILE_EXT = "WAV";             //file extension static
    private static final AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;  //file type as codex to process


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



    public Optional<MultipartFile> process(MultipartFile file) throws IOException {
        java.util.List<Note> notes = new java.util.ArrayList<>();
        PredictResponse response = buildResponse(file);
        StringBuilder sb = new StringBuilder();

        response.getPayloadList().forEach(pl -> {
            sb.append(pl.getDisplayName()).append(" ");
            notes.add(new Note(pl.getDisplayName(), pl.getClassification().getScore()));
        });

        LOG.info("Size: {}, Notes: {}", notes.size(), sb.toString());

        Optional<File> fOpt = Optional.ofNullable(mergeNotes(getClips(notes), notes));

        if(fOpt.isPresent()){
            return Optional.of(new MockMultipartFile("name", new FileInputStream(fOpt.get())));
        }
        return Optional.empty();
    }

    private PredictResponse buildResponse(MultipartFile file) throws IOException {
//        Image img = Image.newBuilder().setImageBytes(ByteString.copyFrom(
//                Files.readAllBytes(loader.getResource("classpath:/maxresdefault.jpg").getFile().toPath()))).build();
        return PredictionServiceClient.create().predict(
                PredictRequest.newBuilder()
                        .putParams("score_threshold", String.valueOf(SCORE_THRESHOLD))
                        .setPayload(ExamplePayload.newBuilder().setImage(Image.newBuilder().setImageBytes(ByteString.copyFrom(file.getBytes())).build()).build())
                        .setName(ModelName.of(PROJECT_ID, LOCATION, VISION_MODEL).toString())
                        .build());
    }


    private File mergeNotes(java.util.List<AudioInputStream> clips, java.util.List<Note> notes) throws IOException {
        if (clips.size() < 2) {
            LOG.error("Too few music files read");
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

//        java.util.Map<String, Note> noteMap = new java.util.HashMap<>();                            // init HashMap
//        notes.stream().filter(n -> !noteMap.containsKey(n.getType())).forEach(n -> noteMap.put(n.getType(), n));    // Add notes to hashmap by key

        try{

            for(Note n : notes){
                musicFiles.add(AudioSystem.getAudioInputStream(ResourceUtils.getFile("classpath:" + n.getType() + "." + FILE_EXT)));
            }

//            for (String key : noteMap.keySet()) {
//                musicFiles.add(AudioSystem.getAudioInputStream(ResourceUtils.getFile("classpath:" + key + "." + FILE_EXT)));
//            }
        }catch(IOException | UnsupportedAudioFileException ie){
            ie.printStackTrace();
        }

        LOG.info("Loaded {} music files", musicFiles.size());

        return musicFiles;
    }
}
