package staff.training.system.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

// 此為報名請求的實體類，用於映射到數據庫中的enrollment_requests表
@Entity
@Table(name = "enrollment_requests")
@Data
public class EnrollmentRequest {
   @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 對應ID 自行生成

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") 
    @JsonIdentityReference(alwaysAsId = false)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;// 外鍵關聯user_id 不可為null

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = false)
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false) 
    private Course course; // 外鍵關聯course_id 不可為null

    @Enumerated(EnumType.STRING)// 將枚舉類型轉換為字符串存儲
    private EnrollmentStatus status = EnrollmentStatus.PENDING; // 報名狀態 默認為PENDING

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date(); // 創建時間

    public enum EnrollmentStatus {
        PENDING, APPROVED, REJECTED // 報名狀態的枚舉類型
    }
    
}