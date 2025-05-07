package staff.training.system.model.dto;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.Data;

import java.time.ZoneId;

// 此為課程進度的DTO類，用於傳遞課程ID、課程標題、章節標題、進度和最後訪問時間
@Data
public class ProgressDTO {
    private Integer courseId;
    private String courseTitle;    
    private String chapterTitle;   
    private Integer progress;      
    private LocalDateTime lastAccessed; 

    // 這裡的progress和lastAccessed都是可選的，進度默認為0，最後訪問時間默認為null
    public ProgressDTO(Integer courseId, String courseTitle, String chapterTitle, Integer progress, Date lastAccessed) {
        this.courseId = courseId; 
        this.courseTitle = courseTitle;
        this.chapterTitle = chapterTitle;
        this.progress = progress != null ? progress : 0; // 如果progress為null，則設置為0
        this.lastAccessed = lastAccessed != null 
            ? lastAccessed.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime() // 將Date轉換為LocalDateTime
            : null;
    }
    public Integer getCourseId() {
        return courseId;
    } 
}