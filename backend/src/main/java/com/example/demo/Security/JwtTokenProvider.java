package com.example.demo.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.SecureDigestAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Component
public class JwtTokenProvider {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    @Value("${jwt.expiration}")
    private long expiration;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    public void initKeys() {
        privateKey = loadPrivateKey();
        publicKey = loadPublicKey();
    }

    private PrivateKey loadPrivateKey() {
        try{
            String privateKeyEnv = getRequiredEnv("PRIVATE_KEY");
            privateKeyEnv = privateKeyEnv
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(privateKeyEnv);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    private PublicKey loadPublicKey() {
        try {
            String publicKeyEnv = getRequiredEnv("PUBLIC_KEY");
            publicKeyEnv = publicKeyEnv
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyEnv);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }


    public String generateToken(String username) {
        String token = "";

        try{
            token = Jwts.builder()
                    .subject(username)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(privateKey,Jwts.SIG.RS512)
                    .compact();
            return token;
        }
        catch(Exception e) {
            logger.error("Error generating token: {}", e.getMessage());
            return "";
        }
    }

    public Claims getClaimsFromToken(String token) {
        try{
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (ExpiredJwtException e) {
            logger.warn("Token has expired: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error parsing token: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }

    }

    public boolean isTokenValid(String token) {
        try{
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date());
        }
        catch (ExpiredJwtException e) {
            logger.warn("Token is invalid because it is expired: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    private String getRequiredEnv(String keyName) {
        String value = System.getenv(keyName);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(keyName + " is not set in the environment variables");
        }
        return value;
    }


}
