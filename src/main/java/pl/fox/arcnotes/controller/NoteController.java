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


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/notes/")
public class NoteController {

    private static final Logger LOG = LoggerFactory.getLogger(NoteController.class);

    private final CloudVisionTemplate template;
    private final ResourceLoader loader;

    @Autowired
    public NoteController(CloudVisionTemplate template, ResourceLoader loader) {
        this.template = template;
        this.loader = loader;
    }

    @GetMapping("/extract")
    public ModelAndView processImage(String imageUrl) {
        AnnotateImageResponse res = template.analyzeImage(loader.getResource(imageUrl), Feature.Type.LABEL_DETECTION);

        Map<String, Float> labels = res.getLabelAnnotationsList().stream().collect(Collectors.toMap(
                EntityAnnotation::getDescription,
                EntityAnnotation::getScore,
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                }, LinkedHashMap::new));

        return new ModelAndView("result", labels);
    }
}
