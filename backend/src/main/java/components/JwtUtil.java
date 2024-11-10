package components;

import java.util.Date;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * Responsible for handling generation and validation of JWTs.
 */
@Component
public class JwtUtil {

    //The SECRET_KEY is stored inside the system variables
    private final String SECRET_KEY = System.getenv("SECRET_KEY");

    private String generateToken(String username) {

        long now = System.currentTimeMillis();
        long expiry = now + 1000 * 60 * 60 * 10;

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expiry))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public String extractUsername(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public boolean isTokenExpired(String token){
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }

    public boolean isTokenValid(String token, String username){
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token)) ;
    }

    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}
