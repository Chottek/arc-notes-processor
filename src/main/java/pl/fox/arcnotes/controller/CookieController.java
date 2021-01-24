package pl.fox.arcnotes.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fox.arcnotes.model.CookieEntity;
import pl.fox.arcnotes.service.CookieService;

@RestController
@RequestMapping("/api/cookieEntities")
public class CookieController {

    private static final String URL = "http://localhost:8080/api/users";

    private final CookieService service;

    @Autowired
    public CookieController(CookieService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CookieEntity ce){
        service.addOrSave(ce);
        return ResponseEntity.created(java.net.URI.create(URL + ce.getCookieId())).build();
    }

    @GetMapping("getByCookie/{id}")
    public ResponseEntity<java.util.List<CookieEntity>> getByCookie(@PathVariable("id") String cookieID){
        return java.util.Optional.ofNullable(service.findAllByCookieId(cookieID)).map(cookieEntities ->
                ResponseEntity.accepted().body(cookieEntities))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("deleteAllByCookie/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String cookieID){
        java.util.Optional<java.util.List<CookieEntity>> ce = java.util.Optional.ofNullable(service.findAllByCookieId(cookieID));
        if(ce.isPresent()){
            for(CookieEntity c : ce.get()){
                service.remove(c);
            }
            return ResponseEntity.accepted().build();
        }else{
            return ResponseEntity.notFound().build();
        }
    }

}
