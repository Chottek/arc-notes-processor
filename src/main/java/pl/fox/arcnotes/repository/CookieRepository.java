package pl.fox.arcnotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.fox.arcnotes.model.CookieEntity;

@Repository
public interface CookieRepository extends JpaRepository<CookieEntity, String> {

    @Query("SELECT ce FROM CookieEntity ce WHERE ce.cookieId=?1")
    java.util.List<CookieEntity> findAllByCookie(String cookieID);

}
