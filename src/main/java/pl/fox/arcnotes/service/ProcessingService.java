package pl.fox.arcnotes.service;

import com.google.cloud.automl.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import pl.fox.arcnotes.model.Note;

import javax.annotation.PostConstruct;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.util.Optional;


@Service
public class ProcessingService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingService.class);

    private static final String PROJECT_ID = "150146461045";             //google project id
    private static final String LOCATION = "us-central1";                //google project location
    private static final String VISION_MODEL = "IOD5055336761211748352"; //google taught vision api serial
    private static final double SCORE_THRESHOLD = 0.6;      //Border value of results score (getAll > SCORE_THRESHOLD)

    private static final String FILE_EXT = "WAV";             //file extension static
    private static final AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;  //file type as codex to process
    private static final String NOTES_PATH = "notes";
    private static final String OUTPUT_PATH = "merged";

    private final java.util.List<Note> notes = new java.util.ArrayList<>();

    private final String[] notesArr = {"C", "D", "E", "F", "G", "A", "H"};


    @PostConstruct
    private void initNotesList() {
        try {
            for (String s : notesArr) {
                notes.add(new Note(s, AudioSystem.getAudioInputStream(
                        ResourceUtils.getFile("classpath:" + NOTES_PATH + "/" + s + "." + FILE_EXT))));
            }
            LOG.info("Finished Notes initializing [size: {}]", notes.size());
        } catch (UnsupportedAudioFileException | IOException e) {
            e.printStackTrace();
        }
    }

    //@TODO: Make use of inited notes

    public Optional<File> process(MultipartFile file) throws IOException {
        java.util.List<Note> notez = new java.util.ArrayList<>();
        PredictResponse response = buildResponse(file);
        StringBuilder sb = new StringBuilder();

        response.getPayloadList().forEach(pl -> {
            sb.append(pl.getDisplayName()).append(" ");
            for (Note n : this.notes) {
                if (n.getType().equals(pl.getDisplayName())) {
                    notez.add(n);
                }
            }
        });

        LOG.info("Size: {}, Notes: {}", notez.size(), sb.toString());

        return Optional.ofNullable(merge(notez));
    }

    //@TODO: Check this page: https://blog.karthicr.com/posts/2013/01/12/concatenate-wav-files-in-java/

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

    private File merge(java.util.List<Note> notes) throws IOException{
        if (notes.size() < 2) {
            LOG.error("Too few notes found to make a music file!");
            return null;
        }

        AudioFormat af = null;
        java.util.List<AudioInputStream> clips = new java.util.ArrayList<>();
        long frameLen = 0;

        for(Note n: notes){
            if(af == null){
                af = n.getSoundFile().getFormat();
            }

            clips.add(n.getSoundFile());
            frameLen += n.getSoundFile().getFrameLength();
        }

        String res = java.util.UUID.randomUUID().toString().concat(FILE_EXT);

        File f = new File(".\\" + OUTPUT_PATH + "\\" + res + "." + FILE_EXT);

        AudioSystem.write(
                new AudioInputStream(
                        new SequenceInputStream(java.util.Collections.enumeration(clips)), af, frameLen),
                FILE_TYPE, f);

        LOG.info("Created new File: {}", f.getAbsolutePath());

        return f;
    }

}
