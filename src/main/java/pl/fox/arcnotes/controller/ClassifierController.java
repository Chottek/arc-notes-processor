package pl.fox.arcnotes.controller;

import com.google.cloud.automl.v1.*;
import com.google.protobuf.ByteString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/classifier")
public class ClassifierController {

    private static final Logger LOG = LoggerFactory.getLogger(ClassifierController.class);

    private static final String PROJECT_ID = "150146461045";             //google project id
    private static final String LOCATION = "us-central1";                //google project location
    private static final String VISION_MODEL = "IOD5055336761211748352"; //google taught vision api serial
    private static final double SCORE_THRESHOLD = 0.6;      //Border value of results score (getAll > SCORE_THRESHOLD)

    private final ResourceLoader loader;

    @Autowired
    public ClassifierController(ResourceLoader loader){
        this.loader = loader;
    }


    @GetMapping("/check")
    public ResponseEntity<String> test(){
        try{
            Image img = Image
                    .newBuilder()
                    .setImageBytes(ByteString.copyFrom(
                            Files.readAllBytes(loader.getResource("classpath:/maxresdefault.jpg").getFile().toPath()))).build();

            ModelName name = ModelName.of(PROJECT_ID, LOCATION, VISION_MODEL);

            PredictResponse res = PredictionServiceClient.create().predict(
                    PredictRequest.newBuilder()
                    .putParams("score_threshold", String.valueOf(SCORE_THRESHOLD))
                    .setPayload(ExamplePayload.newBuilder().setImage(img).build())
                    .setName(name.toString())
                    .build());

            StringBuilder sb = new StringBuilder();
            res.getPayloadList().forEach(pl ->
                sb.append("Name: ").append(pl.getDisplayName()).append(", score: ").append(pl.getClassification().getScore()).append("\n")
            );

            LOG.info(sb.toString());

        }catch(IOException ie){
            return ResponseEntity.badRequest().body(ie.getLocalizedMessage());
        }
        return ResponseEntity.ok("Notes Are Working!");
    }
}
