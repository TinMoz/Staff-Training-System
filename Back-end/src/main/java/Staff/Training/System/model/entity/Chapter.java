package staff.training.system.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;

// 此為章節實體類，對應數據庫中的chapters表
@Entity
@Table(name = "chapters")
@Data
public class Chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; //對應ID 自行生成

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonBackReference 
    @JsonIgnore
    private Course course;  // 外鍵關聯course_id 不可為null

    @Column(nullable = false, length = 200)
    private String title; // 章節標題 不可為null 長度為200

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content; // 章節內容 不可為null 類型為MEDIUMTEXT

    private Integer duration;  // 章節時長

    @Column(name = "order_num")
    private Integer orderNum; // 章節順序

    @Column(name = "weekday",columnDefinition = "TINYINT")
    private Integer weekday;  // 星期幾，1-7分別代表周一到周日 類型為TINYINT

    @Column(name = "start_time", columnDefinition = "TIME")
    private String startTime;  // 開始時間，格式為HH:mm:ss 類型為Time

    @Column(name = "end_time", columnDefinition = "TIME")
    private String endTime;  // 結束時間，格式為HH:mm:ss 類型為Time

    public String getRawStartTime() { // 返回原始的開始時間
        return startTime;
    }

    public String getRawEndTime() { // 返回原始的結束時間
        return endTime;
    }

    // 调整格式化方法并保持JSON序列化
    @JsonFormat(pattern = "HH:mm")
    public String getDisplayStartTime() { // 返回格式化的開始時間，僅顯示小時和分鐘
        return startTime != null ? startTime.substring(0, 5) : null;
    }

    @JsonFormat(pattern = "HH:mm")
    public String getDisplayEndTime() { // 返回格式化的結束時間，僅顯示小時和分鐘
        return endTime != null ? endTime.substring(0, 5) : null;
    }
}


    
