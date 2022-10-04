package auth.service;

import auth.model.User;
import auth.model.UserRegisterDTO;
import auth.security.JwtTokenProvider;
import auth.security.WebSecurityConfig;
import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final FirebaseService firebaseService;

    public ResponseEntity<?> login(String client, String secret) {
        try {
            //TODO check if user exists in firebase and secret is right
            //TODO fix the exceptions

            Firestore firestore = firebaseService.getApp();
            DocumentReference docRef = firestore.collection("user").document(client);
            ApiFuture<DocumentSnapshot> future = docRef.get();
            System.out.println(future.get().getData().get("secret"));
            if(future.get().getData() == null) {
                throw new NoSuchElementException("User not found");
            } else if (!passwordEncoder.matches(secret, (String) future.get().getData().get("secret"))) {
                throw new NoSuchElementException("wrong password");
            }

            LOGGER.debug("m=Login succeeded");
            return new ResponseEntity<>(jwtTokenProvider.createToken(client), HttpStatus.OK);
        } catch (NoSuchElementException | InterruptedException | ExecutionException e) {
            LOGGER.error("m=Login stage=error stacktrace={}", e.getStackTrace());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    public ResponseEntity<?> validate(String token) {
        return new ResponseEntity<>(jwtTokenProvider.validateToken(token), HttpStatus.OK);
    }

    public ResponseEntity<?> register(UserRegisterDTO user) {

        try {

            Firestore firestore = firebaseService.getApp();

            Map<String, String> docData = new HashMap<>();

            docData.put("secret", passwordEncoder.encode(user.getSecret()));

            ApiFuture<WriteResult> future = firestore.collection("user").document(user.getClient()).set(docData, SetOptions.merge());

            return new ResponseEntity<>(future, HttpStatus.OK);
        } catch ( Exception e) {
            LOGGER.error("m=Register stage=error stacktrace={}" + e.getStackTrace());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    public void test(String test) throws Exception {

        Firestore firestore = firebaseService.getApp();
        DocumentReference docRef = firestore.collection("user").document(test);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        Object vsf = future.get().getData();
        if(future.get().getData() == null) {
            throw new NoSuchElementException("vai se lascar java");
        }
//        System.out.println(data.getClass());

//        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
//        for (QueryDocumentSnapshot document : documents) {
//            System.out.println(document.getId() + " => " + document.toObject(User.class));
//        }
    }
}
