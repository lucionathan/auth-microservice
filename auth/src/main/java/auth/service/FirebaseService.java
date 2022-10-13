package auth.service;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.google.auth.oauth2.ServiceAccountCredentials.fromStream;
import static com.google.firebase.FirebaseApp.initializeApp;
import static com.google.firebase.cloud.FirestoreClient.*;

@Service
public class FirebaseService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String SERVICE_ACCOUNT_FILE = "/serviceAccount.json";

    private Firestore app;

    public FirebaseService() {
        logger.debug("m=Firebase stage=init");
        try {
            var inputStream = this.getClass().getResourceAsStream(SERVICE_ACCOUNT_FILE);
            var options = FirebaseOptions.builder()
                    .setCredentials(fromStream(inputStream))
                    .build();

            initializeApp(options);
            app = getFirestore();
            logger.info("m=Firebase stage=end");
        } catch (IOException e) {
            logger.error("m=Firebase stage=error on connect to firebase stacktrace={}" , e.getMessage(), e);
            System.exit(0);
        }
    }

    public Firestore getApp() {
        return app;
    }
}
