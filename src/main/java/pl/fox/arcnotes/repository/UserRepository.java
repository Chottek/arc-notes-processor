package pl.fox.arcnotes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.fox.arcnotes.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    //All things are in JpaRepository here

    @Query("SELECT u.cookieId FROM User u")
    java.util.List<User> findCookie();

    User findOneByCookieId(String cookieId);

}
