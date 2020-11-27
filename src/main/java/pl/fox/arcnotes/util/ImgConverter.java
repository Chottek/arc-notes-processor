package pl.fox.arcnotes.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class ImgConverter {

    private static final Logger LOG = LoggerFactory.getLogger(ImgConverter.class);

    public static BufferedImage readByteArray(byte[] arr){
        try {
            return ImageIO.read(new ByteArrayInputStream(arr));
        } catch (IOException e) {
            e.printStackTrace();
        }
         return null;
    }

    public static byte[] compressBytes(byte[] data) {
        Deflater deflater = new Deflater();
        deflater.setInput(data);
        deflater.finish();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];
        while (!deflater.finished()) {
            int count = deflater.deflate(buffer);
            outputStream.write(buffer, 0, count);
        }
        try {
            outputStream.close();
        } catch (IOException e) {
            LOG.error("Something went wrong compressing data!");
        }
        LOG.info("Compressed Image - {}", outputStream.toByteArray().length);

        return outputStream.toByteArray();

    }


    // uncompress the image bytes before returning it to the angular application

    public static byte[] decompressBytes(byte[] data) {
        Inflater inflater = new Inflater();
        inflater.setInput(data);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
        byte[] buffer = new byte[1024];

        try {
            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            outputStream.close();
        } catch (IOException | DataFormatException ioe) {
            LOG.error("Something went wrong decompressing bytes");
        }

        return outputStream.toByteArray();
    }

}
