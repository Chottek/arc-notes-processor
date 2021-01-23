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

    public User(String cookieId, byte[] file) {
        this.cookieId = cookieId;
        this.file = file;
    }

    public User(){

    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return java.util.Objects.equals(cookieId, user.cookieId) &&
                java.util.Arrays.equals(file, user.file);
    }

    @Override
    public int hashCode() {
        int result = java.util.Objects.hash(cookieId);
        result = 31 * result + java.util.Arrays.hashCode(file);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "cookieId='" + cookieId + '\'' +
                ", file length=" + file.length +
                '}';
    }
}
