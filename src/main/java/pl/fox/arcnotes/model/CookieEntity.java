package pl.fox.arcnotes.model;


import javax.persistence.Lob;

/**
 * @author Chottek
 * User Entity to store image in database based on cookie-id
 */
@javax.persistence.Entity
@javax.persistence.Table(name = "cookieuser")
public class CookieEntity {

    private Integer id;
    private String cookieId;
    private byte[] file;

    public CookieEntity(String cookieId, byte[] file) {
        this.cookieId = cookieId;
        this.file = file;
    }

    public CookieEntity() {

    }

    @javax.persistence.Id
    @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.AUTO)
    public Integer getId(){
        return id;
    }

    public String getCookieId() {
        return cookieId;
    }

    @Lob  //large object
    public byte[] getFile() {
        return file;
    }

    public void setId(Integer id) {
        this.id = id;
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
        CookieEntity cookieEntity = (CookieEntity) o;
        return  java.util.Objects.equals(id, cookieEntity.id) &&
                java.util.Objects.equals(cookieId, cookieEntity.cookieId) &&
                java.util.Arrays.equals(file, cookieEntity.file);
    }

    @Override
    public int hashCode() {
        int result =  java.util.Objects.hash(id, cookieId);
        result = 31 * result + java.util.Arrays.hashCode(file);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", cookieId='" + cookieId + '\'' +
                ", file.length=" + file.length +
                '}';
    }
}
