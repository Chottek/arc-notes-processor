package pl.fox.arcnotes.controller;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import pl.fox.arcnotes.model.ImageArray;
import pl.fox.arcnotes.service.NoteService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/notes/")
public class NoteController {

    private static final Logger LOG = LoggerFactory.getLogger(NoteController.class);
    private final NoteService service;
    private final CloudVisionTemplate template;
    private final ResourceLoader loader;

    @Autowired
    public NoteController(NoteService service, CloudVisionTemplate template, ResourceLoader loader) {
        this.service = service;
        this.template = template;
        this.loader = loader;
    }

    @GetMapping("/extract")
    public ModelAndView processImage(String imageUrl){
        AnnotateImageResponse res = template.analyzeImage(loader.getResource(imageUrl), Feature.Type.LABEL_DETECTION);

        Map<String, Float> labels = res.getLabelAnnotationsList().stream().collect(Collectors.toMap(
                EntityAnnotation::getDescription,
                EntityAnnotation::getScore,
                (u, v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); }, LinkedHashMap::new));

        return new ModelAndView("result", labels);
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

    @GetMapping("/g")
    public String test() throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new ClassPathResource("maxresdefault.jpg").getFile());

        return service.visionIt(bufferedImage);
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
