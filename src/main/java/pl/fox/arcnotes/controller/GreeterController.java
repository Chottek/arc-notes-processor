package pl.fox.arcnotes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
public class GreeterController {

    @GetMapping
    public ResponseEntity<String> greet(){
        return ResponseEntity.ok("Henlo Almighty Klałd");
    }

}
