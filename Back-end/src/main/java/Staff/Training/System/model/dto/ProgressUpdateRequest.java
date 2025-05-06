package staff.training.system.model.dto;
import lombok.Data;

@Data
public class ProgressUpdateRequest {
    private Integer courseId; // 改为Integer类型
    private Integer progress; 
}