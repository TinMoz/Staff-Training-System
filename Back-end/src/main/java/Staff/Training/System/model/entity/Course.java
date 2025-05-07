package staff.training.system.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

//此為課程的實體類，用於映射到數據庫中的courses表
@Entity
@Table(name = "courses")
@Data
public class Course {
    @Id
    @Getter
    @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // 對應ID

    @Column(nullable = false, length = 200)
    private String title; // 課程標題 不可為null 長度為200

    @Lob  // 对应 SQL 的 TEXT 类型
    @Column(nullable = false, length = 500)
    private String description; // 課程描述 不可為null 長度為500

    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;  // 創建者ID 外鍵關聯user_id 不可為null

    @Column(name = "created_at", insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt; // 創建時間 默認當前時間 不可添加或更新

    @Column(name = "teacher")
    private String teacher;  // 教師名字

    @Getter
    @Setter
    @OneToMany(mappedBy = "course" ,cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Chapter> chapters = new ArrayList<>(); // 章節列表 一對多關聯
    
    @Getter
    @Setter
    private String courseCode; // 課程代碼
    private Integer credits; // 學分

    @Getter
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnrollmentRequest> enrollmentRequests = new ArrayList<>(); // 課程報名請求列表 一對多關聯

    // 此為課程內的DTO類，用於傳遞課程ID、課程標題、章節列表、創建時間和教師名字
    @Data
    public static class CourseDetailDTO {
        private Integer id; // 課程ID
        private String title; // 課程標題
        private String description; // 課程描述
        private String courseCode;  // 課程代碼
        private int credits;// 學分

        
        @Column(name = "created_at", insertable = false, updatable = false)
        @Temporal(TemporalType.TIMESTAMP)
        private List<ChapterDTO> chapters; // 章節列表

        @Column(name = "created_at", insertable = false, updatable = false)
        @Temporal(TemporalType.TIMESTAMP)
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private Date createdAt; // 創建時間
        private String teacher; // 教師名字
    
        public CourseDetailDTO(
            Integer id, // 課程ID
            String title, // 課程標題
            String courseCode, // 課程代碼
            int credits, // 學分
            List<Chapter> chapters, // 章節列表
            String description, // 課程描述
            Date createdAt,  // 創建時間
            String teacher  // 教師名字
        ) {
            this.id = id;
            this.title = title;
            this.courseCode = courseCode;
            this.credits = credits;
            this.description = description;
            this.createdAt = createdAt;
            this.chapters = chapters.stream() // 透過Stream 將Chapter轉換為ChapterDTO實體
                .map(ch -> new ChapterDTO(ch.getId(), ch.getTitle(), ch.getContent(), ch.getOrderNum(), ch.getDuration(), ch.getWeekday(), ch.getRawStartTime(), ch.getRawEndTime()))
                .toList();
            this.teacher = teacher;
        }
        /// 此為課程內的DTO類，用於傳遞章節ID、章節標題、章節內容、章節順序、章節時長、星期幾、開始時間和結束時間
        @Data
        public static class ChapterDTO {
            private Integer id; // 章節ID
            private String title; // 章節標題
            private String content; // 章節內容
            private Integer orderNum; // 章節順序
            private Integer duration; // 章節時長
            private Integer weekday; // 星期幾，1-7分別代表周一到周日
            private String startTime; // 開始時間，格式為HH:mm:ss
            private String endTime; // 結束時間，格式為HH:mm:ss

    
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