package auth.controller;

import auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestHeader String client, @RequestHeader String secret) {
        return authService.login(client, secret);
    }

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestHeader String token) {
        return authService.validate(token);
    }
}
