package pl.fox.arcnotes.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fox.arcnotes.ByteUtils;
import pl.fox.arcnotes.model.CookieEntity;
import pl.fox.arcnotes.repository.CookieRepository;

/**
 * @author Chottek
 * Service that handles CookieEntity management
 */
@Service
public class CookieService {

    private final CookieRepository repository;

    @Autowired
    public CookieService(CookieRepository repository) {
        this.repository = repository;
    }

    /**
     * Method used to find all objects by cookieID
     * @param cookieID String that contains cookie ID, given to RestController as parameter
     * @return Entities list with decompressed byte array
     */
    public java.util.List<CookieEntity> findAllByCookieId(String cookieID){
        java.util.List<CookieEntity> entities = repository.findAllByCookie(cookieID);
        entities.forEach(ce -> ce.setFile(ByteUtils.decompressBytes(ce.getFile())));

        return entities;
    }

    /**
     * Method used to remove all objects by cookieID
     * @param cookieID To specify which cookieID do we want to remove
     */
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
