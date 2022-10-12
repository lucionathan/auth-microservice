package auth.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {

    @JsonProperty("token_type")
    private String type;

    @JsonProperty("access_token")
    private String token;

    @JsonProperty("expires_in")
    private Long expiresIn;
}
