package pl.fox.arcnotes.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WelcomeController {

    @RequestMapping(path="hello")
    public String sayHello(){
        return "Welcome to arc-notes processing application";
    }
}
