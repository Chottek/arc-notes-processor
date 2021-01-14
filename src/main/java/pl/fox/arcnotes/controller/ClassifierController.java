package pl.fox.arcnotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.fox.arcnotes.model.Note;
import pl.fox.arcnotes.service.ProcessingService;

import java.io.IOException;

@RestController
@RequestMapping("/api/classifier")
public class ClassifierController {

    private final ProcessingService service;

    @Autowired
    public ClassifierController(ProcessingService service){
        this.service = service;
    }

    @GetMapping("/check")
    public ResponseEntity<String> test(){
        java.util.List<Note> s;
        try{
            s = service.process();
        }catch(IOException ie){
            return ResponseEntity.badRequest().body(ie.getLocalizedMessage());
        }
        return ResponseEntity.ok("Notes Are Working! Length: " + s.size());
    }
}
