package staff.training.system.model.dto;
import lombok.Data;

// 此為課程進度請求的DTO類，用於傳遞課程ID和進度信息
@Data
public class ProgressRequestDTO {
    private Integer courseId; 
    private Integer progress; 
}