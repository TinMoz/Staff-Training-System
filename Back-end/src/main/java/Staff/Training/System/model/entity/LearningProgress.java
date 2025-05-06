package staff.training.system.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;


@Entity
@Table(name = "learning_progress")
@Data
@Getter
public class LearningProgress  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;  // 外键关联到users.id
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Course course;  // 外键关联到courses.id

    @ManyToOne
    @JoinColumn(name = "chapter_id", nullable = false)
    private Chapter chapter;  // 外键关联到 chapters.id

    @Column(columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean completed;

    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer progress;  // 百分比

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastAccessed;
}