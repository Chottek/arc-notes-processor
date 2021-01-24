package pl.fox.arcnotes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fox.arcnotes.model.CookieEntity;
import pl.fox.arcnotes.repository.CookieRepository;

@Service
public class CookieService {

    private final CookieRepository repository;

    @Autowired
    public CookieService(CookieRepository repository) {
        this.repository = repository;
    }

    public java.util.List<CookieEntity> findAllByCookieId(String cookieID){
        return repository.findAllByCookie(cookieID);
    }

    public void addOrSave(CookieEntity u){
        repository.save(u);
    }

    public void remove(CookieEntity u){
        repository.delete(u);
    }

}
