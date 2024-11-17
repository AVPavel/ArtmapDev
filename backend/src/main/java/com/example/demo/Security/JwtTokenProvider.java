package com.example.demo.Security;

import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {
    /**
     * logger variable
     */
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    /**
     * Expiration time in milliseconds
     */
    @Value("${jwt.expiration}")
    private long expiration;

    /**
     * Private key used for generating the token
     */
    private PrivateKey privateKey;

    /**
     * Public key used for validating the JWT
     */
    private PublicKey publicKey;

    /**
     * Initializes the private and public keys
     */
    @PostConstruct
    public void initKeys() {
        privateKey = loadPrivateKey();
        publicKey = loadPublicKey();
    }

    /**
     * Loads the private key from the system environment variable named "PRIVATE_KEY" and generates a {@link PrivateKey} object.
     * The private key is expected to be in PEM format, and this method decodes and parses it into a Java {@link PrivateKey}.
     *
     * @return a {@link PrivateKey} instance generated from the environment variable.
     * @throws RuntimeException if there is an issue with the key's format or the algorithm used to generate it.
     */
    private PrivateKey loadPrivateKey() {
        try {
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

    /**
     * Loads the public key from the system environment variable named "PUBLIC_KEY" and generates a {@link PublicKey} object.
     * The public key is expected to be in PEM format, and this method decodes and parses it into a Java {@link PublicKey}.
     *
     * @return a {@link PublicKey} instance generated from the environment variable.
     * @throws RuntimeException if there is an issue with the key's format or the algorithm used to generate it.
     */
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

    /**
     * Generates a 10-hour available JWT for the {@param username} provided using RS512 Algorithm from {@link Jwts.SIG}
     *
     * @param username Username for which the token is generated
     * @return the token as a {@link String}
     */
    public String generateToken(String username) {

        try {
            return Jwts.builder()
                    .subject(username)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiration))
                    .signWith(privateKey, Jwts.SIG.RS512)
                    .compact();
        } catch (Exception e) {
            logger.error("Error generating token: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Gets the payload / claims of the JWT (defined by the JWT specification RFC 7519)
     *
     * @param token The JWT as a {@link String}
     * @return The entire payload of the JWT
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            logger.warn("Token has expired: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error parsing token: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }

    }

    /**
     * Verifies is the token is valid by checking if it is expired
     *
     * @param token The token as a {@link String}
     * @return true if the token is valid | false if the token is expired
     */
    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            logger.warn("Token is invalid because it is expired: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Token validation failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets the username from the token's Claims
     *
     * @param token The Token as a {@link String}
     * @return The username (subject) from the token
     */
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    /**
     * Retrieves the value of a specified environment variable.
     * <p>
     * This method searches for an environment variable by its name (`keyName`) and returns its value.
     * If the environment variable is not set or its value is empty, an {@link IllegalStateException} is thrown.
     * </p>
     *
     * @param keyName the name of the environment variable to retrieve
     * @return the value of the specified environment variable
     * @throws IllegalStateException if the environment variable is not set or its value is empty
     */
    private String getRequiredEnv(String keyName) {
        String value = System.getenv(keyName);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(keyName + " is not set in the environment variables");
        }
        return value;
    }


}
