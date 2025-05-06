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


@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;  // 对应 SQL 的 INT 类型

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false, length = 100)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Getter // 添加
    @Setter // 添加
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Role role;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("user")
    private List<LearningProgress> progresses = new ArrayList<>();

    
    @OneToMany(mappedBy = "user")
    @JsonIgnoreProperties("user")
    private List<EnrollmentRequest> enrollmentRequests = new ArrayList<>();

    // 时间戳默认值
    @Column(insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;  // SQL 默认 CURRENT_TIMESTAMP


}