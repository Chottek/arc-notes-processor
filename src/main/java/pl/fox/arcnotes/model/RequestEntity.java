package pl.fox.arcnotes.model;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.springframework.web.multipart.MultipartFile;

public class RequestEntity {

    @JsonProperty("cookieID")
    private String cookieID;

    @JsonProperty("photoFile")
    private MultipartFile photoFile;

    public RequestEntity(String cookieID, MultipartFile photoFile) {
        this.cookieID = cookieID;
        this.photoFile = photoFile;
    }

    @JsonGetter
    public String getCookieID() {
        return cookieID;
    }
    @JsonGetter
    public MultipartFile getPhotoFile() {
        return photoFile;
    }

    @JsonSetter
    public void setCookieID(String cookieID) {
        this.cookieID = cookieID;
    }

    @JsonSetter
    public void setPhotoFile(MultipartFile photoFile) {
        this.photoFile = photoFile;
    }

    @Override
    public String toString() {
        return "RequestEntity{" +
                "cookieID='" + cookieID + '\'' +
                ", photoFile [size]=" + photoFile.getSize() +
                '}';
    }
}
