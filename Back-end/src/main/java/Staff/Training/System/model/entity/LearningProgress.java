package staff.training.system.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

// 此為學習進度的實體類，用於存儲用戶的學習進度信息
@Entity
@Table(name = "learning_progress")
@Data
@Getter
public class LearningProgress  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 對應ID 自行生成

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;  // 外鍵關聯到users.id 不可為null
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;  // 外鍵關聯到courses.id 不可為null

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;  // 外鍵關聯到chapters.id 不可為null

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean completed; // 是否完成 預設為False

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer progress;  // 百分比 預設為0

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAccessed; // 最後訪問時間
}