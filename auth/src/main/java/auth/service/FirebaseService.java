package auth.service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class FirebaseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FirebaseService.class);


    public static final String REALTIME_DATABASE_URL = "https://test-55d75.firebaseio.com";

    public static final String SERVICE_ACCOUNT_FILE = "serviceAccount.json";
    private Firestore app;

    public FirebaseService() {
        LOGGER.debug("m=Firebase stage=init");
        try {
            var classloader = Thread.currentThread().getContextClassLoader();
            var file = new File(classloader.getResource(SERVICE_ACCOUNT_FILE).getFile());
            var serviceAccount = new FileInputStream(file);

            FirebaseOptions options = FirebaseOptions.builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

            FirebaseApp.initializeApp(options);
            app = FirestoreClient.getFirestore();


        } catch (IOException e) {
            LOGGER.error("m=Firebase stage=error stacktrace={}" , e.getStackTrace());
            System.exit(0);
        }
        LOGGER.debug("m=Firebase stage=end");
    }

    public Firestore getApp() {
        return app;
    }
}
