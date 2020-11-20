package pl.fox.arcnotes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/base")
public class BaseController {

    @GetMapping
    public ResponseEntity<String> greetTheOne(){
        return ResponseEntity.ok("Greetings to the almighty Cloud!");
    }

}
