package pl.fox.arcnotes.model;

import com.google.cloud.firestore.annotation.IgnoreExtraProperties;

import java.sql.Date;
import java.util.List;
import java.util.Objects;

@IgnoreExtraProperties
public class ImageArray {

    private String name;
    private Date date;

    //Could not serialize object. Serializing Arrays is not supported, please use Lists instead
    //private byte[] image;
    private List<Integer> image;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public List<Integer> getImage() {
        return image;
    }

    public void setImage(List<Integer> image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageArray that = (ImageArray) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(date, that.date) &&
                Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, date, image);
    }

    @Override
    public String toString() {
        return "ImageArray{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", image=" + image +
                '}';
    }
}
