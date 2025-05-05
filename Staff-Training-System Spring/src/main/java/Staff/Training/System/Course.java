package Staff.Training.System;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.BatchSize;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@Table(name = "courses")
@Data
public class Course {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 200)
    private String title;

    @Lob  // 对应 SQL 的 TEXT 类型
    @Column(nullable = false, length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;  // 外键关联到 users.id

    @Column(name = "created_at", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "teacher")
    private String teacher;  // 教师姓名

    @Getter
    @Setter
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 50) 
    @JsonManagedReference
    private List<chapter> chapters = new ArrayList<>();
    
    @Getter
    @Setter
    private String courseCode;
    private Integer credits;

    @Getter
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnrollmentRequest> enrollmentRequests = new ArrayList<>();


    @Data
    public static class CourseDetailDTO {
        private Integer id;
        private String title;
        private String description;
        @Lob
        private String courseCode;
        private int credits;

        
        @Column(name = "created_at", insertable = false, updatable = false)
        @Temporal(TemporalType.TIMESTAMP)
         // 新增字段
        private List<ChapterDTO> chapters;

        @Column(name = "created_at", insertable = false, updatable = false)
        @Temporal(TemporalType.TIMESTAMP)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private Date createdAt;
        private String teacher;
    
        public CourseDetailDTO(
            Integer id,
            String title,
            String courseCode,
            int credits,
            List<chapter> chapters,
            String description,
            Date createdAt, 
             String teacher  // 改为 teacher 保持一致性
        ) {
            this.id = id;
            this.title = title;
            this.courseCode = courseCode;
            this.credits = credits;
            this.description = description;
            this.createdAt = createdAt;
            this.chapters = chapters.stream()
                .map(ch -> new ChapterDTO(ch.getId(), ch.getTitle(), ch.getContent(), ch.getOrderNum(), ch.getDuration(), ch.getWeekday(), ch.getRawStartTime(), ch.getRawEndTime()))
                .toList();
            this.teacher = teacher;
        }
    
        @Data
        public static class ChapterDTO {
            private Integer id;
            private String title;
            private String content;
            private Integer orderNum;
            private Integer duration;
            private Integer weekday;
            private String startTime;
            private String endTime;

    
            public ChapterDTO(Integer id, String title, String content, Integer orderNum, Integer duration, Integer weekday, String startTime, String endTime) {
                this.id = id;
                this.title = title;
                this.content = content;
                this.orderNum = orderNum;
                this.duration = duration;
                this.weekday = weekday;
                this.startTime = startTime;
                this.endTime = endTime;
            }
        }
    }
}