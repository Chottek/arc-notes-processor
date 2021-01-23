package pl.fox.arcnotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String cookieId){
        java.util.Optional<User> user = java.util.Optional.ofNullable(service.findFirstByCookieId(cookieId));
        if(user.isPresent()){
            service.remove(user.get());
            return ResponseEntity.accepted().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

}
