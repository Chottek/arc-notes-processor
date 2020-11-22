package pl.fox.arcnotes.service;


import com.google.cloud.vision.v1.ImageAnnotatorClient;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

@Service
public class BaseService {

    public void serve(){
        try {
            var image = ImageIO.read(new File("elo.png"));

            var client = ImageAnnotatorClient.create();

            var response = client.getStub();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
