package Staff.Training.System.Progress;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

import java.time.ZoneId;


@Data
public class ProgressDTO {
    private Integer courseId;
    private String courseTitle;    // 对应 c.title
    private String chapterTitle;   // 对应 ch.title
    private Integer progress;       // 对应 lp.progress
    private LocalDateTime lastAccessed; // 对应 lp.lastAccessed
    

    // 必须的构造函数（参数顺序和类型需与查询一致）
    public ProgressDTO(Integer courseId, String courseTitle, String chapterTitle, Integer progress, Date lastAccessed) {
        this.courseId = courseId; 
        this.courseTitle = courseTitle;
        this.chapterTitle = chapterTitle;
        this.progress = progress != null ? progress : 0; // 处理空值
        this.lastAccessed = lastAccessed != null 
            ? lastAccessed.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
            : null;
        // 初始化为null
    }
    public Integer getCourseId() {
        return courseId;
    } 
}