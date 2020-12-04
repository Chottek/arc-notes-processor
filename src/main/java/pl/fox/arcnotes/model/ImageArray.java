package pl.fox.arcnotes.model;

import java.sql.Date;
import java.util.Arrays;
import java.util.Objects;


public class ImageArray {

    private String name;
    private Date date;
    private byte[] image;


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

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageArray that = (ImageArray) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(date, that.date) &&
                Arrays.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(name, date);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "ImageArray {" +
                "name='" + name + '\'' +
                ", date=" + date +
                ",\n image=" + Arrays.toString(image) +
                '}';
    }
}
