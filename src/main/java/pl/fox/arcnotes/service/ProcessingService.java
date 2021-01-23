package pl.fox.arcnotes.service;

import com.google.cloud.automl.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pl.fox.arcnotes.model.Note;
import pl.fox.arcnotes.repository.NoteRepository;

import java.io.File;
import java.io.IOException;

/**
 * @author Chottek
 * Service class that processes the input file,
 * sends it to Google Vision and merges music files in return
 */

@Service
public class ProcessingService {

    private static final Logger LOG = LoggerFactory.getLogger(ProcessingService.class);

    private static final String PROJECT_ID = "150146461045";             //google project id
    private static final String LOCATION = "us-central1";                //google project location
    private static final String VISION_MODEL = "IOD5055336761211748352"; //google taught vision api serial
    private static final double SCORE_THRESHOLD = 0.6;      //Border value of results score (getAll > SCORE_THRESHOLD)

    private final NoteRepository repository;

    @Autowired
    public ProcessingService(NoteRepository repository){
        this.repository = repository;
    }

    /**
      * @param file From POSTMapping of ClassifierController class
     * @return Optional with processed music file or null
     * @throws IOException - of file reading exception
     */
    public java.util.Optional<File> process(MultipartFile file) throws IOException {
        java.util.List<Note> notez = new java.util.ArrayList<>();
        PredictResponse response = buildResponse(file);
        StringBuilder sb = new StringBuilder();

        response.getPayloadList().forEach(pl -> {
            sb.append(pl.getDisplayName()).append(" ");
            for (Note n : repository.getNotes()) {
                if (n.getType().equals(pl.getDisplayName())) {
                    notez.add(n);
                }
            }
        });

        LOG.info("Size: {}, Notes: {}", notez.size(), sb.toString());

        return java.util.Optional.ofNullable(repository.merge(notez));
    }

    /**
     * Method used to build Prediction of Google VISION API
     * @param file Image with notes
     * @return Built prediction request
     * @throws IOException - of File reading exception
     */
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


}
