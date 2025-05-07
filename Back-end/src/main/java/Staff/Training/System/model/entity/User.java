package staff.training.system.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;
import lombok.Getter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// 此為用戶的實體類，用於映射到數據庫中的users表
@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // 對應ID 自行生成

    @Column(nullable = false, unique = true, length = 50)
    private String username; // 用戶名，唯一且不可為null,長度限制50

    @Column(nullable = false, length = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password; // 密碼，不可為null，寫入時不返回

    @Column(nullable = false, unique = true, length = 100)
    private String email; // 電子郵件，唯一且不可為null,長度限制100

    @Enumerated(EnumType.STRING)
    @Getter 
    @Setter
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Role role; // 用戶權限，枚舉類型，讀取時返回
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("user")
    private List<LearningProgress> progresses = new ArrayList<>(); // 用戶的學習進度列表

    
    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    private List<EnrollmentRequest> enrollmentRequests = new ArrayList<>(); // 用戶的報名請求列表


    @Column(insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt; // 創建時間，插入時自動生成，不可更新


}