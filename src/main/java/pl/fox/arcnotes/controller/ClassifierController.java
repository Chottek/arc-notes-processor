package pl.fox.arcnotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.fox.arcnotes.service.ProcessingService;

@RestController
@RequestMapping("/api/classifier")
public class ClassifierController {

    private final ProcessingService service;

    @Autowired
    public ClassifierController(ProcessingService service){
        this.service = service;
    }

    @PostMapping("/process")
    public ResponseEntity process(@RequestBody MultipartFile file){
        try{
            java.util.Optional<java.io.File> op = service.process(file);

            if(op.isPresent()){
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + op.get().getName() + "\"")
                        .body(op.get());
            }
        }catch(java.io.IOException ie){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There was a problem processing file");
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Too few notes to process");
    }
}
