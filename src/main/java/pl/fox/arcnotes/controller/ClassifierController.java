package pl.fox.arcnotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.fox.arcnotes.model.RequestEntity;
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
     * @param requestEntity Multipart file that contains notes image
     * @return ResponseEntity with music file or with return ERROR information
     */
    @PostMapping(value = "/process", consumes = {"multipart/form-data"})
    public ResponseEntity<java.io.File> process(@ModelAttribute RequestEntity requestEntity, @RequestPart("photoFile") MultipartFile file){
        try{
            java.util.Optional<java.io.File> op = service.process(requestEntity);

            if(op.isPresent()){
                return ResponseEntity.accepted()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + op.get().getName() + "\"")
                        .body(op.get());
            }
        }catch(java.io.IOException ie){
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.noContent().build();
    }
}
