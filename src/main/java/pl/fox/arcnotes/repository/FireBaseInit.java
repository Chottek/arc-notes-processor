package pl.fox.arcnotes.repository;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;

@Service
public class FireBaseInit {

    private static final Logger LOG = LoggerFactory.getLogger(FireBaseInit.class);

    private static final String SERVICE_KEY = "arc-notes-processor-firebase-adminsdk-ppy6o-f9a1c7eeb5.json";
    private static final String DB_URL = "https://arc-notes-processor.firebaseio.com";

    @PostConstruct
    public void initialize() {
        try {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(SERVICE_KEY).getInputStream()))
                    .setDatabaseUrl(DB_URL)
                    .build();

            if (FirebaseApp.getApps().isEmpty()) { //<--- check with this line
                FirebaseApp.initializeApp(options);
            }

        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}


