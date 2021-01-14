package pl.fox.arcnotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.fox.arcnotes.service.ProcessingService;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/classifier")
public class ClassifierController {

    private final ProcessingService service;

    @Autowired
    public ClassifierController(ProcessingService service){
        this.service = service;
    }

    @PostMapping("/process")
    public ResponseEntity<MultipartFile> process(@RequestBody MultipartFile file){
        MultipartFile f;
        try{
           f = service.process(file).get();
        }catch(IOException | InterruptedException | ExecutionException | UnsupportedAudioFileException ie){
            return ResponseEntity.of(java.util.Optional.empty());
        }
        return ResponseEntity.ok().body(f);
    }
}
