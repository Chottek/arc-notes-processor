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
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ProcessingService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingService.class);

    private static final String PROJECT_ID = "150146461045";             //google project id
    private static final String LOCATION = "us-central1";                //google project location
    private static final String VISION_MODEL = "IOD5055336761211748352"; //google taught vision api serial
    private static final double SCORE_THRESHOLD = 0.6;      //Border value of results score (getAll > SCORE_THRESHOLD)

    private static final String FILE_EXT = "WAV";             //file extension static
    private static final AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;  //file type as codex to process

    public Optional<File> process(MultipartFile file) throws IOException {
        java.util.List<Note> notes = new java.util.ArrayList<>();
        PredictResponse response = buildResponse(file);
        StringBuilder sb = new StringBuilder();

        response.getPayloadList().forEach(pl -> {
            sb.append(pl.getDisplayName()).append(" ");
            notes.add(new Note(pl.getDisplayName(), pl.getClassification().getScore()));
        });

        LOG.info("Size: {}, Notes: {}", notes.size(), sb.toString());

        return Optional.ofNullable(mergeNotes(getClips(notes), notes));
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


    private File mergeNotes(java.util.Map<String, AudioInputStream> audioMap, java.util.List<Note> notes) throws IOException {
        if (notes.size() < 2) {
            LOG.error("Too few notes found to make a music file!");
            return null;
        }

        String res = java.util.UUID.randomUUID().toString().concat(FILE_EXT);
        AudioInputStream files = null;

        List<AudioInputStream> clips = new java.util.ArrayList<>();

        for(Note n: notes){
            clips.add(audioMap.get(n.getType()));
        }

        LOG.info("Loaded audioclips: {}", clips.size());

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

    private java.util.Map<String, AudioInputStream> getClips(java.util.List<Note> notes) {
        if(notes.size() < 2){
            return null;
        }

        java.util.Map<String, AudioInputStream> noteMap = new java.util.HashMap<>();  // init HashMap

        try{
            for(Note n : notes){
                AudioInputStream ais = AudioSystem.getAudioInputStream(ResourceUtils.getFile("classpath:" + n.getType() + "." + FILE_EXT));
                if(!noteMap.containsKey(n.getType())){
                    noteMap.put(n.getType(), ais);
                }
            }
        }catch(IOException | UnsupportedAudioFileException ie){
            ie.printStackTrace();
        }

        LOG.info("Loaded {} music files", noteMap.keySet().size());

        return noteMap;
    }
}
