package pl.fox.arcnotes.model;


/**
 * @author Chottek
 * User Entity to store image in database based on cookie-id
 */
@javax.persistence.Entity
@javax.persistence.Table(name = "users")
public class User {

   private String cookieId;
   private byte[] file;

    @javax.persistence.Id
    public String getCookieId(){
        return cookieId;
    }

    public byte[] getFile() {
        return file;
    }

    public void setCookieId(String cookieId) {
        this.cookieId = cookieId;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
