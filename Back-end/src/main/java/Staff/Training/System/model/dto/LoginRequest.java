package staff.training.system.model.dto;

import lombok.Getter;
import lombok.Setter;

public class LoginRequest {
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