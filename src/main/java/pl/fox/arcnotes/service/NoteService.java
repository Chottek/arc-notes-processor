package pl.fox.arcnotes.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.ImageProperties;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pl.fox.arcnotes.model.ImageArray;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;

@Service
public class NoteService {

    private static final String COLLECTION = "images";
    private final CloudVisionTemplate template;

    @Autowired
    public NoteService(CloudVisionTemplate template){
        this.template = template;
    }

    public String saveImage(ImageArray ia) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection(COLLECTION).document(ia.getName()).set(ia);
        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public ImageArray getByName(String name) throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection(COLLECTION).document(name);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot document = future.get();

        if (document.exists()) {
            return document.toObject(ImageArray.class);
        }
        return null;
    }

    public String visionIt(BufferedImage image){
        AnnotateImageResponse res = template.analyzeImage((Resource) image, Feature.Type.IMAGE_PROPERTIES);
        ImageProperties properties = res.getImagePropertiesAnnotation();

        return properties.toString();
    }
}
