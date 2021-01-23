package pl.fox.arcnotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.fox.arcnotes.model.User;
import pl.fox.arcnotes.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final String URL = "http://localhost:8080/api/users";

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody User u){
        service.addOrSave(u);
        return ResponseEntity.created(java.net.URI.create(URL + u.getCookieId())).build();
    }


}
