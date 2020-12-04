package pl.fox.arcnotes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fox.arcnotes.model.ImageArray;
import pl.fox.arcnotes.service.NoteService;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/api/notes/")
public class NoteController {

    private static final Logger LOG = LoggerFactory.getLogger(NoteController.class);
    private final NoteService service;

    @Autowired
    public NoteController(NoteService service){
        this.service = service;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestBody ImageArray ia) throws ExecutionException, InterruptedException {
        LOG.info("IMAGE BYTESIZE: {}", ia.getImage().length);
        return ResponseEntity.ok(service.saveImage(ia));
    }

    @GetMapping("/get")
    public ImageArray getImage(@RequestParam String name) throws InterruptedException, ExecutionException{
        return service.getByName(name);
    }

    @PostConstruct
    public String save() throws IOException, ExecutionException, InterruptedException {
        var imageArr = new ImageArray();
        var date = new Date(1607104675770L);

        imageArr.setName("TEST");
        imageArr.setDate(date);
        imageArr.setImage(extractBytes("maxresdefault.jpg"));
        return service.saveImage(imageArr);
    }

    //TEMPORARY / JUST FOR TESTING PURPOSES
    public byte[] extractBytes (String imageName) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ClassPathResource(imageName).getFile());

        // get DataBufferBytes from Raster
        WritableRaster raster = bufferedImage .getRaster();
        DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

        return data.getData();
    }

}
