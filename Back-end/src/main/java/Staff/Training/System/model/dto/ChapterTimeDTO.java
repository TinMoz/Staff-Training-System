package staff.training.system.model.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

// 此為課程時間表的DTO類，用於傳遞課程時間和章節信息
@Data
@AllArgsConstructor
public class ChapterTimeDTO {
    private Integer courseId;
    private String courseTitle;
    private Integer weekday;
    private String startTime;
    private String endTime;
    private String chapterTitle;
}
