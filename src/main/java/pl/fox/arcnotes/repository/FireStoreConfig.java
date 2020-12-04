package pl.fox.arcnotes.repository;

import com.google.api.client.util.Value;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class FireStoreConfig {

    @Value("${spring.gcp.firestore.projectid}")
    private String projectId;

    @Bean
    public Firestore FirestoreDb() throws IOException {
        // Use the application default credentials
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(projectId)
                .build();
        FirebaseApp.initializeApp(options); //<-------- Here

        return FirestoreClient.getFirestore();
    }
}
