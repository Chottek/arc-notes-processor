package pl.fox.arcnotes.controller;

import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vision/")
public class VisionController {

    private final CloudVisionTemplate template;
    private final ResourceLoader loader;

    @Autowired
    public VisionController(CloudVisionTemplate template, ResourceLoader loader){
        this.template = template;
        this.loader = loader;
    }

    @RequestMapping("/getLabelDetection")
    public String getLabelDetection(){
        return template.analyzeImage(loader.getResource("/cat.jpg"), Feature.Type.LABEL_DETECTION)
                .getLabelAnnotationsList()
                .toString();
    }
}
