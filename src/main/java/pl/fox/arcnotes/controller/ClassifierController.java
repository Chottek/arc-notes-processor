package pl.fox.arcnotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.fox.arcnotes.model.RequestEntity;
import pl.fox.arcnotes.service.ProcessingService;

import java.io.FileInputStream;

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
    public ResponseEntity<Resource> process(@ModelAttribute RequestEntity requestEntity, @RequestPart("photoFile") MultipartFile file){
        try{
            java.util.Optional<java.io.File> op = service.process(requestEntity);

            if(op.isPresent()){
                InputStreamResource resource = new InputStreamResource(new FileInputStream(op.get()));
                HttpHeaders headers = new HttpHeaders(); headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+op.get().getName());
                return ResponseEntity.ok()
                        .headers(headers)
                        .contentLength(op.get().length())
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            }
        }catch(java.io.IOException ie){
            return ResponseEntity.unprocessableEntity().build();
        }
        return ResponseEntity.noContent().build();
    }
}
