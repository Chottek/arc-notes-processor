package pl.fox.arcnotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.fox.arcnotes.service.ProcessingService;

/**
 * @author Chottek
 * RestController on "/api/classifier" mapping
 * that handles POST Http request and passes it to
 * Autowired ProcessingService class
 */

@RestController
@RequestMapping("/api/classifier")
public class ClassifierController {

    private final ProcessingService service;

    @Autowired
    public ClassifierController(ProcessingService service){
        this.service = service;
    }

    /**
     * Method that passes file to service and returns processed music file
     * @param file Multipart file that contains notes image
     * @return ResponseEntity with music file or with return ERROR information
     */
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
