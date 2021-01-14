package pl.fox.arcnotes;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@SpringBootApplication
public class ArcApplication {


    public static void main(String[] args) throws IOException {
        gAuth();
        SpringApplication.run(ArcApplication.class, args);
    }

    private static void gAuth() throws IOException {
//        File accountJson = ResourceUtils.getFile("classpath:service-account.json");
//        // You can specify a credential file by providing a path to GoogleCredentials.
//        // Otherwise credentials are read from the GOOGLE_APPLICATION_CREDENTIALS environment variable.
//        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(accountJson));
//        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }
}
