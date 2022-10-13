package auth.controller;

import auth.model.Token;
import auth.model.UserRegister;
import auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final AuthService auth;

    @PostMapping("/login")
    public ResponseEntity<Token> login(@RequestHeader("Authorization") String authHeader) {
        try {
            var token = auth.login(authHeader);
            return new ResponseEntity<>(token, OK);
        } catch (Exception e) {
            logger.error("m=login stage=error e={}", e.getMessage(), e);
            return new ResponseEntity<>(NOT_FOUND);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Void> validate(@RequestHeader("Authorization") String token) {
        try {
            auth.validate(token);
            return new ResponseEntity<>(NO_CONTENT);
        } catch (Exception e) {
            logger.error("m=validate stage=error e={}", e.getMessage(), e);
            return new ResponseEntity<>(UNAUTHORIZED);
        }
    }

    @DeleteMapping("/credential/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id, @RequestHeader("Authorization") String authorization) {
        try {
            auth.delete(id, authorization);
            return new ResponseEntity<>(NO_CONTENT);
        } catch (Exception e) {
            logger.error("m=delete stage=error e={}", e.getMessage(), e);
            return new ResponseEntity<>(UNAUTHORIZED);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody UserRegister user, @RequestHeader("Authorization") String authorization) {
        try {
            auth.register(user, authorization);
            return new ResponseEntity<>(NO_CONTENT);
        } catch (Exception e) {
            logger.error("m=register stage=error e={}", e.getMessage(), e);
            return new ResponseEntity<>(BAD_REQUEST);
        }
    }

}
