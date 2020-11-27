package pl.fox.arcnotes.util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImgConverter {

    public static BufferedImage readByteArray(byte[] arr){
        try {
            return ImageIO.read(new ByteArrayInputStream(arr));
        } catch (IOException e) {
            e.printStackTrace();
        }
         return null;
    }

}
