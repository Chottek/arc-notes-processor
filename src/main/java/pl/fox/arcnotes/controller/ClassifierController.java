package pl.fox.arcnotes.controller;

import com.google.cloud.automl.v1.Image;
import com.google.cloud.automl.v1.ModelName;
import com.google.protobuf.ByteString;
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

    private static final String PROJECT_ID = "150146461045";
    private static final String LOCATION = "us-central1";
    private static final String VISIONMODEL = "IOD5055336761211748352";

    @GetMapping("/check")
    public ResponseEntity<Void> test(){
        try{
            ByteString imgBytes = ByteString.copyFrom(Files.readAllBytes(Paths.get("classpath:/maxresdefault.jpg")));
            Image img = Image.newBuilder().setImageBytes(imgBytes).build();
            ModelName name = ModelName.of(PROJECT_ID, LOCATION, VISIONMODEL);
            //@TODO: Implement rest of data gathering from vision
        }catch(IOException ie){
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }



}
