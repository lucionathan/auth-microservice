package auth.service;

import auth.model.User;
import auth.security.JwtTokenProvider;
import com.google.api.core.ApiFuture;
import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.firestore.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final JwtTokenProvider jwtTokenProvider;

    private final FirebaseService firebaseService;

    public ResponseEntity<?> login(String client, String secret) {
        try {
            //TODO check if user exists in firebase and secret is right
            LOGGER.debug("m=Login");
            return new ResponseEntity<>(jwtTokenProvider.createToken(client), HttpStatus.OK);
        } catch (NotFoundException e) {
            LOGGER.error("m=Login stage=error stacktrace={}" + e.getStackTrace());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    public ResponseEntity<?> validate(String token) {
        return new ResponseEntity<>(jwtTokenProvider.validateToken(token), HttpStatus.OK);
    }

    public void register(String client, String secret) throws Exception {

        Firestore firestore = firebaseService.getApp();

        Map<String, Object> docData = new HashMap<>();

        docData.put("password", secret);

        ApiFuture<WriteResult> future = firestore.collection("user").document(client).set(docData);
        System.out.println("Update time : " + future.get().getUpdateTime());
    }
    
    public void test(String test) throws Exception {

        Firestore firestore = firebaseService.getApp();
        DocumentReference docRef = firestore.collection("user").document(test);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        System.out.println("Document data: " + document.getData());

//        List<QueryDocumentSnapshot> documents = future.get().getDocuments();
//        for (QueryDocumentSnapshot document : documents) {
//            System.out.println(document.getId() + " => " + document.toObject(User.class));
//        }
    }
}
