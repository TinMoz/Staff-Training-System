package staff.training.system.model.dto;

import lombok.Getter;
import lombok.Setter;

// 此為註冊請求的DTO類，用於傳遞用戶名、密碼和電子郵件
@Getter
@Setter
public class RegisterRequestDTO {
    private String username;
    private String password;
    private String email;
}