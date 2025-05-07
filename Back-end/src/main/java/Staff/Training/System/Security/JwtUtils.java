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

// 此為JWT工具類，用於生成和驗證JWT令牌
@Component
public class JwtUtils {
    // 定義256位的SecretKey，用於簽名和驗證JWT令牌
    private static final SecretKey SECURITY_KEY = 
        Keys.hmacShaKeyFor("your-256-bit-secret-key-must-be-long-enough".getBytes(StandardCharsets.UTF_8));

    // 定義一個日誌記錄器Logger，用於記錄JWT相關的錯誤信息
    private static final Logger logger = (Logger) LoggerFactory.getLogger(JwtUtils.class);

    // 生成JWT令牌的Method，傳入用戶名、用戶ID和角色
    public String generateToken(String username, Integer userId, String role) {
        return Jwts.builder() // 使用Jwts.builder()創建JWT令牌
            .setSubject(username) // 設置物件為用戶名
            .claim("userId", userId) // 添加用戶ID聲明
            .claim("role", role) // 添加用戶權限聲明
            .claim("username", username)  // 添加username声明
            .setIssuedAt(new Date()) // 設置簽發時間為當前時間
            .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 設置過期時間為24小時後
            .signWith(SECURITY_KEY, io.jsonwebtoken.SignatureAlgorithm.HS256) // 使用HS256算法簽名
            .compact(); // 返回生成的JWT令牌
    }
    
    
    // 解析JWT令牌，獲取用戶名
    public String getUsernameFromToken(String token) {
        try {
            return parseClaims(token).get("username", String.class); // 使用parseClaims方法解析JWT令牌，並獲取用戶名
        } catch (JwtException e) {
            logger.error("Failed to extract username from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }
    }
    // 解析JWT令牌，獲取用戶權限
    public String getRoleFromToken(String token) {
        try {
            return parseClaims(token).get("role", String.class); // 使用parseClaims方法解析JWT令牌，並獲取用戶權限
        } catch (JwtException e) {
            logger.error("Failed to extract role from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }
    }
    // 解析JWT令牌，獲取用戶ID
    public Integer getUserIDFromToken(String token) {
        try {
            return parseClaims(token).get("userId", Integer.class); // 使用parseClaims方法解析JWT令牌，並獲取用戶ID
        } catch (JwtException e) {
            logger.error("Failed to extract userID from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }
    }


    // 驗證JWT令牌的有效性，返回true或false
    public boolean validateToken(String token) {
        try {
            Jwts.parser() // 使用Jwts.parser()解析JWT令牌
                .setSigningKey(SECURITY_KEY) // 設置簽名密鑰
                .build() // 創建解析器
                .parseClaimsJws(token); // 解析JWT令牌
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
    // 解析JWT令牌，獲取Claims物件
    private Claims parseClaims(String token) {
        return Jwts.parser() // 使用Jwts.parser()解析JWT令牌
                .setSigningKey(SECURITY_KEY) // 設置簽名密鑰
                .build() // 創建解析器
                .parseClaimsJws(token) // 解析JWT令牌
                .getBody(); // 返回Claims物件
    }

}

