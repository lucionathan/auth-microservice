package auth.service;

import auth.security.JwtTokenProvider;
import com.google.api.gax.rpc.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final JwtTokenProvider jwtTokenProvider;

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
}
