package auth.security;

import auth.model.Token;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Objects;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.util.Base64.*;
import static java.util.Objects.*;

@Component
public class JwtTokenProvider {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @Value("${security.jwt.token.expire-length:3600000}")
    private long validityInMilliseconds;

    @PostConstruct
    protected void init() {
        secretKey = getEncoder().encodeToString(secretKey.getBytes());
    }

    public Token createToken(String username, String scope) {
        logger.debug("m=login stage=init username={}", username);

        var claims = Jwts.claims()
                .setSubject(username);
        var now = new Date();
        var validity = new Date(now.getTime() + validityInMilliseconds);

        var tokenGenerated = Jwts.builder()
                .setClaims(claims)
                .claim("scope", scope)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(HS256, secretKey)
                .compact();

        var token = Token.builder()
                .token(tokenGenerated)
                .type("Bearer")
                .expiresIn(validity.getTime())
                .build();
        logger.info("m=login stage=end token={}", token);
        return token;
    }

    public Claims getClaims(String token) {
        logger.debug("m=getUsername stage=init token={}", token);
        var claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();

        logger.info("m=getUsername stage=end claims={}", claims);
        return claims;
    }

    public String resolveToken(String bearerToken) {
        logger.debug("m=resolveToken stage=init bearerToken={}", bearerToken);
        if (!isNull(bearerToken) && bearerToken.startsWith("Bearer ")) {
            var token = bearerToken.split(" ");
            logger.info("m=resolveToken stage=end token={}", token[1]);
            return token[1];
        }
        logger.info("m=resolveToken stage=end token cannot be resolved");
        return null;
    }

    public void validateToken(String token) {
        logger.debug("m=validateToken stage=init token={}", token);
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            logger.info("m=validateToken stage=end token valid");
        } catch (ExpiredJwtException e) {
            logger.info("m=validateToken stage=error token expired e={}", e.getMessage(), e);
            throw e;
        } catch (JwtException | IllegalArgumentException e) {
            logger.info("m=validateToken stage=error generic error e={}", e.getMessage(), e);
            throw e;
        }
    }
}