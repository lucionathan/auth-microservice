package auth.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UserRegister {
    private String clientId;
    private String clientSecret;
}
