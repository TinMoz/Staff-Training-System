package Staff.Training.System;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "chapters")
@Data
public class chapter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    @JsonBackReference 
    @JsonIgnore
    private Course course;  // 外键关联到 courses.id

    @Column(nullable = false, length = 200)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "MEDIUMTEXT")
    private String content;

    private Integer duration;  // 分钟

    @Column(name = "order_num")
    private Integer orderNum;

    @Column(name = "weekday",columnDefinition = "TINYINT")
    private Integer weekday;  // 星期几，0-6表示周日到周六

    @Column(name = "start_time", columnDefinition = "TIME")
    private String startTime;  // 开始时间，格式为HH:mm:ss

    @Column(name = "end_time", columnDefinition = "TIME")
    private String endTime;  // 结束时间，格式为HH:mm:ss

    public String getRawStartTime() {
        return startTime;
    }

    public String getRawEndTime() {
        return endTime;
    }

    // 调整格式化方法并保持JSON序列化
    @JsonFormat(pattern = "HH:mm")
    public String getDisplayStartTime() {
        return startTime != null ? startTime.substring(0, 5) : null;
    }

    @JsonFormat(pattern = "HH:mm")
    public String getDisplayEndTime() {
        return endTime != null ? endTime.substring(0, 5) : null;
    }
}


    
