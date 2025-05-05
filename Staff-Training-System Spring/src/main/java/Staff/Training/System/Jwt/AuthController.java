package Staff.Training.System.Jwt;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import Staff.Training.System.LoginRequest;
import Staff.Training.System.RegisterRequestDTO;
import Staff.Training.System.User;
import Staff.Training.System.role;
import Staff.Training.System.Repository.UserRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequest) {
        // 用户名唯一性校验
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            return ResponseEntity.badRequest().body("用户名已被注册");
        }
        
        // 邮箱唯一性校验
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            return ResponseEntity.badRequest().body("邮箱已被注册");
        }
    
        // 创建新用户
        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setEmail(registerRequest.getEmail());
        newUser.setRole(role.USER); // 设置默认角色
        newUser.setCreatedAt(new java.util.Date()); // 设置创建时间
    
        userRepository.save(newUser);
        
        return ResponseEntity.ok(Map.of(
            "message", "注册成功",
            "username", newUser.getUsername()
        ));
    }



    @PostMapping("/login") 
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        // 验证用户名是否存在
        User user = userRepository.findByUsername(loginRequest.getUsername())
            .orElseThrow(() -> new RuntimeException("用户不存在"));
            System.out.println("Input password: " + loginRequest.getPassword());
            System.out.println("Stored password hash: " + user.getPassword());
            System.out.println("Password match: " + passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()));

        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            System.out.println("Input password: " + loginRequest.getPassword());
            System.out.println("Stored password hash: " + user.getPassword());
            System.out.println("Password match: " + passwordEncoder.matches(loginRequest.getPassword(), user.getPassword()));
            return ResponseEntity.status(403).body("用户名或密码错误");
            
        }

        // 生成 Token
        String token = jwtUtils.generateToken(
            user.getUsername(),
            user.getId(),
            user.getRole().name()
        );

        return ResponseEntity.ok(Map.of(
            "token", token,
            "role", user.getRole().name(),
            "username", user.getUsername()
            
        ));
    }
        
}