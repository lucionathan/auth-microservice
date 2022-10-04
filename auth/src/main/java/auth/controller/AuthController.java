package auth.controller;

import auth.model.UserRegisterDTO;
import auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


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

    @GetMapping
    public void test() throws Exception {
        authService.test("test");
    }

    @PutMapping("")
    public ResponseEntity<?> register(@RequestBody UserRegisterDTO user) throws Exception {
        return authService.register(user);
    }

}
