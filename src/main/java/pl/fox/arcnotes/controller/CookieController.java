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

    /**
     * Save [Add or save changes] method
     * @param ce CookieEntity that contains cookieId and filebyte array
     * @return ResponseEntity containing information that Object was saved
     */
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody CookieEntity ce){
        service.addOrSave(ce);
        return ResponseEntity.created(java.net.URI.create(URL + ce.getCookieId())).build();
    }


    /**
     * Method getting every Object that contains id given in param
     * @param cookieID Id by which the objects will be gathered
     * @return ResponseEntity with body of java.util.List containg CookieEntities
     */
    @GetMapping("getByCookie/{id}")
    public ResponseEntity<java.util.List<CookieEntity>> getByCookie(@PathVariable("id") String cookieID){
        return java.util.Optional.ofNullable(service.findAllByCookieId(cookieID)).map(cookieEntities ->
                ResponseEntity.accepted().body(cookieEntities))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    /**
     * Method deleting every Object that contains id given in param
     * @param cookieID id by which objects will be deleted
     * @return ResponseEntity with successful deleting
     */
    @DeleteMapping("deleteAllByCookie/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") String cookieID){
        service.removeAllByCookieId(cookieID);
        return ResponseEntity.accepted().build();
    }

}
