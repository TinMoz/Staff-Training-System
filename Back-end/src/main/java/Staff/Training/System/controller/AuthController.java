package staff.training.system.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import staff.training.system.model.dto.LoginRequestDTO;
import staff.training.system.model.dto.RegisterRequestDTO;
import staff.training.system.model.entity.User;
import staff.training.system.model.entity.Role;
import staff.training.system.repository.UserRepository;
import staff.training.system.security.JwtUtils;
import java.util.Map;

// 此Controller提供了用戶註冊和登錄的API接口，處理Auth請求
@RestController // 返回Json格式的響應
@RequestMapping("/api/auth") // 設置請求路徑為/api/auth
public class AuthController {
    @Autowired
    private UserRepository userRepository; // 用戶數據庫操作接口

    @Autowired
    private PasswordEncoder passwordEncoder; // 密碼加密器

    @Autowired
    private JwtUtils jwtUtils; // JWT工具類，用於生成和驗證Token


    @PostMapping("/register") // 處理Post請求，註冊新用戶
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequest) { //傳遞註冊DTO對象
        // 用户名唯一性检查
        if (userRepository.existsByUsername(registerRequest.getUsername())) { //如果用戶名已存在，則返回錯誤信息
            return ResponseEntity.badRequest().body("用户名已被注册");
        }
        
        // 電郵唯一性檢查
        if (userRepository.existsByEmail(registerRequest.getEmail())) { //如果電郵已存在，則返回錯誤信息
            return ResponseEntity.badRequest().body("邮箱已被注册");
        }
    
        // 創建新用戶
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername()); // 設置用戶名
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // 設置加密後的密碼
        newUser.setEmail(registerRequest.getEmail()); // 設置電郵
        newUser.setRole(Role.USER); // 設置用戶角色為普通用戶
        newUser.setCreatedAt(new java.util.Date()); // 設置創建時間
    
        userRepository.save(newUser); // 保存新用戶到數據庫
        
        return ResponseEntity.ok(Map.of( 
            "message", "注册成功",
            "username", newUser.getUsername()
        ));
    }


    @PostMapping("/login") // 處理Post請求，用戶登錄
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) { //傳遞登錄DTO對象
        User user = userRepository.findByUsername(loginRequest.getUsername()) // 根據用戶名查找用戶
            .orElseThrow(() -> new RuntimeException("用户不存在")); // 如果用戶不存在，則拋出異常

        // 驗證密碼
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(403).body("用户名或密码错误"); // 如果密碼不匹配，則返回403錯誤
            
        }

        // 生成 Token
        String token = jwtUtils.generateToken( 
            user.getUsername(), // 用戶名
            user.getId(), // 用戶ID
            user.getRole().name() // 用戶權限
        );

        return ResponseEntity.ok(Map.of(
            "token", token,
            "role", user.getRole().name(),
            "username", user.getUsername()
            
        ));
    }
        
}