package staff.training.system.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ch.qos.logback.classic.Logger;


@Component
public class JwtUtils {
    private static final SecretKey SECURITY_KEY = Keys.hmacShaKeyFor("your-256-bit-secret-key-must-be-long-enough".getBytes(StandardCharsets.UTF_8));
    //private static final Long EXPIRATION_MS = 3600000L;
    private static final Logger logger = (Logger) LoggerFactory.getLogger(JwtUtils.class);

    public String generateToken(String username, Integer userId, String role) {
        return Jwts.builder()
            .setSubject(username)
            .claim("userId", userId)
            .claim("role", role)
            .claim("username", username)  // 添加username声明
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 86400000))
            .signWith(SECURITY_KEY, io.jsonwebtoken.SignatureAlgorithm.HS256)
            .compact();
    }
    
    
    
    public String getUsernameFromToken(String token) {
        try {
            return parseClaims(token).get("username", String.class);
        } catch (JwtException e) {
            logger.error("Failed to extract username from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }
    }

    public String getRoleFromToken(String token) {
        try {
            return parseClaims(token).get("role", String.class);
        } catch (JwtException e) {
            logger.error("Failed to extract role from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }
    }

    public Integer getUserIDFromToken(String token) {
        try {
            return parseClaims(token).get("userId", Integer.class);
        } catch (JwtException e) {
            logger.error("Failed to extract userID from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }
    }



    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .setSigningKey(SECURITY_KEY)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .setSigningKey(SECURITY_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}

