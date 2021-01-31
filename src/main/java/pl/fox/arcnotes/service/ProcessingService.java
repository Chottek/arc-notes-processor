package pl.fox.arcnotes.service;

import com.google.cloud.automl.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fox.arcnotes.ByteUtils;
import pl.fox.arcnotes.model.Note;
import pl.fox.arcnotes.model.RequestEntity;
import pl.fox.arcnotes.model.CookieEntity;
import pl.fox.arcnotes.repository.NoteRepository;

import java.io.File;
import java.util.Objects;

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
    private static final String VISION_MODEL_1 = "IOD6745734733847592960"; //second vision api model serial
    private static final double SCORE_THRESHOLD = 0.5;      //Border value of results score (getAll > SCORE_THRESHOLD)

    private final NoteRepository repository;
    private final CookieService cookieService;

    @Autowired
    public ProcessingService(NoteRepository repository, CookieService cookieService){
        this.repository = repository;
        this.cookieService = cookieService;
    }

    /**
      * @param entity From POSTMapping of ClassifierController class
     * @return Optional with processed music file or null
     * @throws java.io.IOException - of file reading exception
     */
    public java.util.Optional<File> process(RequestEntity entity) throws java.io.IOException {
       LOG.info("Got {}", entity);

       java.util.List<Note> notez = new java.util.ArrayList<>();

        cookieService.addOrSave(new CookieEntity(entity.getCookieID(), ByteUtils.compressBytes(entity.getPhotoFile().getInputStream().readAllBytes()))); //COMPRESS HERE

        StringBuilder sb = new StringBuilder();

        buildResponse(entity.getPhotoFile().getInputStream().readAllBytes()).getPayloadList().forEach(pl -> {
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
     * @param photoFileBytes Bytes array from image with notes
     * @return Built prediction request
     * @throws java.io.IOException - of File reading exception
     */
    private PredictResponse buildResponse(byte[] photoFileBytes) throws java.io.IOException {
//        Image img = Image.newBuilder().setImageBytes(ByteString.copyFrom(
//                Files.readAllBytes(loader.getResource("classpath:/maxresdefault.jpg").getFile().toPath()))).build();
        return PredictionServiceClient.create().predict(
                PredictRequest.newBuilder()
                        .putParams("score_threshold", String.valueOf(SCORE_THRESHOLD))
                        .setPayload(ExamplePayload.newBuilder().setImage(Image.newBuilder().setImageBytes(ByteString.copyFrom(photoFileBytes)).build()).build())
                        .setName(ModelName.of(PROJECT_ID, LOCATION, VISION_MODEL_1).toString())
                        .build());
    }


}
