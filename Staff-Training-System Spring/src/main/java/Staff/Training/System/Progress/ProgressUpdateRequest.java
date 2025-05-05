package Staff.Training.System.Progress;
import lombok.Data;

@Data
public class ProgressUpdateRequest {
    private Integer courseId; // 改为Integer类型
    private Integer progress; 
}