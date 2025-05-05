package Staff.Training.System;

import lombok.AllArgsConstructor;
import lombok.Data;

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
