package staff.training.system.model.dto;

import lombok.Getter;
import lombok.Setter;

//此為登入請求的DTO類，用於傳遞用戶名、密碼和權限信息
public class LoginRequestDTO {
    @Setter
    @Getter
    private String username;

    @Setter
    @Getter
    private String password;

    @Setter
    @Getter
    private String role; 
}