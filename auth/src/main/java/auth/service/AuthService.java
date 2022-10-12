package auth.service;

import auth.model.Token;
import auth.model.UserRegister;
import auth.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static com.google.cloud.firestore.SetOptions.*;
import static java.nio.charset.StandardCharsets.*;
import static java.util.UUID.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String SCOPE = "scope";

    public static final String USERS = "users";

    public static final String CLIENT_SECRET = "clientSecret";

    public static final String CLIENT_ID = "clientId";

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final FirebaseService firebaseService;

    public Token login(String authorization) throws ExecutionException, InterruptedException {
        logger.debug("m=login stage=init authorization={}", authorization);

        var credentials = decodeBasicCredential(authorization);
        var client = credentials[0];
        var secret = credentials[1];

        var firestore = firebaseService.getApp();
        var future = firestore.collection(USERS)
                .whereEqualTo(CLIENT_ID, client)
                .whereEqualTo(CLIENT_SECRET, secret)
                .get();
        var documentsFounded = future.get().getDocuments();

        if (documentsFounded.isEmpty()) {
            logger.debug("m=login stage=error User not found clientId={} clientSecret={}", client, secret);
            throw new NoSuchElementException("User not found");
        }

        logger.info("m=Login stage=end");
        return jwtTokenProvider.createToken(documentsFounded.get(0).getId(), (String) documentsFounded.get(0).get(SCOPE));
    }

    private String[] decodeBasicCredential(String authorization) {
        logger.debug("m=decodeBasicCredential stage=init authorization={}", authorization);
        var base64Credentials = authorization.substring("Basic".length())
                .trim();
        var credDecoded = Base64.getDecoder()
                .decode(base64Credentials);
        var credentials = new String(credDecoded, UTF_8);
        // credentials = username:password
        var credentialsList = credentials.split(":", 2);
        logger.info("m=decodeBasicCredential stage=end credentials={}", credentials);
        return credentialsList;
    }

    public void delete(String client, String token) {
        checkUser(token);
        var firestore = firebaseService.getApp();
        firestore.collection(USERS)
                .document(client)
                .delete();
        logger.info("m=Login succeeded");
    }

    public void validate(String token) {
        var tokenSplit = token.split(" ");
        jwtTokenProvider.validateToken(tokenSplit[1]);
    }

    public void register(UserRegister user, String token) {
        logger.debug("m=register stage=init user={} token={}", user, token);
        checkUser(token);

        var firestore = firebaseService.getApp();
        Map<String, String> data = new HashMap<>();
        data.put(CLIENT_ID, user.getClientId());
        data.put(CLIENT_SECRET, user.getClientSecret());
        data.put(SCOPE, "user");

        firestore.collection(USERS).
                document(randomUUID().toString()).
                set(data, merge());

        logger.info("m=register stage=end");
    }

    private void checkUser(String token) {
        logger.debug("m=checkUser stage=init token={}", token);
        var claims = jwtTokenProvider.getClaims(jwtTokenProvider.resolveToken(token));

        if (!claims.get(SCOPE).equals("full")) {
            logger.error("m=checkUser stage=error User without admin permissions claims={}", claims);
            throw new IllegalArgumentException("User not authorized to register");
        }

        logger.info("m=checkUser stage=end");
    }
}
