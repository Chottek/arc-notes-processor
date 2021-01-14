package pl.fox.arcnotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.fox.arcnotes.model.Note;
import pl.fox.arcnotes.service.ProcessingService;

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

    @GetMapping("/process")
    public ResponseEntity<String> process(){
        java.util.List<Note> s;
        try{
            s = service.process().get();
        }catch(IOException | InterruptedException | ExecutionException ie){
            return ResponseEntity.badRequest().body(ie.getLocalizedMessage());
        }
        return ResponseEntity.ok("Notes Are Working! Length: " + s.size());
    }
}
