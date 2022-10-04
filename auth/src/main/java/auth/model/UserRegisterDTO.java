package auth.model;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String client;
    private String secret;

}
