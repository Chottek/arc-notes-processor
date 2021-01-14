package pl.fox.arcnotes.service;

import com.google.cloud.automl.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.fox.arcnotes.model.Note;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.nio.file.Files;
import java.util.List;
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

    @Async
    public CompletableFuture<List<Note>> process(MultipartFile file) throws IOException{
        java.util.List<Note> notes = new java.util.ArrayList<>();
        PredictResponse response = buildResponse(file);
        StringBuilder sb = new StringBuilder();

        response.getPayloadList().forEach(pl -> {
            sb.append(pl.getDisplayName()).append(" ");
            notes.add(new Note(pl.getDisplayName(), pl.getClassification().getScore()));
        });

        LOG.info("Size: {}, Notes: {}",notes.size(), sb.toString());

        return CompletableFuture.completedFuture(notes);
    }

    private PredictResponse buildResponse(MultipartFile file) throws IOException{
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

    private String returnFile(java.util.List<AudioInputStream> clips) {
        if (clips.size() == 0 || clips.size() == 1) {
            return null;
        }

        String res = java.util.UUID.randomUUID().toString().concat(FILE_EXT);
        AudioInputStream appendedFiles = null;

        for (int i = 0; i < clips.size() - 1; i++) {
            if (i == 0) {
                appendedFiles = new AudioInputStream(
                        new SequenceInputStream(clips.get(i), clips.get(i + 1)), clips.get(i).getFormat(),
                        clips.get(i).getFrameLength() + clips.get(i + 1).getFrameLength());
                continue;
            }
            appendedFiles = new AudioInputStream(
                    new SequenceInputStream(appendedFiles, clips.get(i + 1)), appendedFiles.getFormat(),
                            appendedFiles.getFrameLength() + clips.get(i + 1).getFrameLength());
        }
        try {
            assert appendedFiles != null;
            AudioSystem.write(appendedFiles, FILE_TYPE, new java.io.File(res));
        } catch (IOException ie) {
            LOG.error("{}", ie.getMessage());
        }
        return res;
    }


}
