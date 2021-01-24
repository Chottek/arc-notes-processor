package pl.fox.arcnotes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fox.arcnotes.ByteUtils;
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
        java.util.List<CookieEntity> entities = repository.findAllByCookie(cookieID);
        entities.forEach(ce -> ce.setFile(ByteUtils.decompressBytes(ce.getFile())));

        return entities;
    }

    public void removeAllByCookieId(String cookieID){
        repository.findAllByCookie(cookieID).forEach(this::remove);
    }

    public void addOrSave(CookieEntity ce){
        repository.save(ce);
    }

    public void remove(CookieEntity ce){
        repository.delete(ce);
    }

}
