package pl.fox.arcnotes.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.fox.arcnotes.model.ImageArray;
import pl.fox.arcnotes.service.NoteService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        LOG.info("IMAGE BYTESIZE: {}", ia.getImage().size());
        return ResponseEntity.ok(service.saveImage(ia));
    }

    @GetMapping("/get")
    public ImageArray getImage(@RequestParam String name) throws InterruptedException, ExecutionException{
        return service.getByName(name);
    }


    //@TODO: Learn how to store images instead of byte array, getting "UNAVAILABLE: 413:Request Entity Too Large"

    //TEMPORARY / JUST FOR TESTING PURPOSES
    @GetMapping("/testIt")
    public String save() throws IOException, ExecutionException, InterruptedException {
        var imageArr = new ImageArray();
        var date = new Date(1607104675770L);

        imageArr.setName("TEST");
        imageArr.setDate(date);
        List<Integer> s = new ArrayList<>();

        for(byte b : extractBytes("maxresdefault.jpg")){
            s.add(Byte.toUnsignedInt(b));
        }

        LOG.info("{}", s.size());

        imageArr.setImage(s);

        service.saveImage(imageArr);
        return "HENLO";
    }

    public byte[] extractBytes (String imageName) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ClassPathResource(imageName).getFile());

        WritableRaster raster = bufferedImage.getRaster();
        DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

        return data.getData();
    }
}
