package staff.training.system.controller;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import staff.training.system.model.dto.ChapterTimeDTO;
import staff.training.system.model.dto.ProgressDTO;
import staff.training.system.model.dto.ProgressUpdateRequest;
import staff.training.system.security.JwtUtils;
import staff.training.system.service.ProgressService;




@RestController
@RequestMapping("/api/progress")

public class ProgressController{
    @Autowired
    private ProgressService progressService;
    @Autowired
    private JwtUtils jwtUtils;
    
    @PutMapping
    public ResponseEntity<Void> updateProgress(
        @RequestBody ProgressUpdateRequest request,
        @RequestHeader("Authorization") String token) {
    
        Integer userID = jwtUtils.getUserIDFromToken(token.replace("Bearer ", ""));
        // 添加类型转换
        progressService.updateProgress(userID.intValue(), request.getCourseId().intValue(), request.getProgress());
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/courses")
    public ResponseEntity<List<ProgressDTO>> getUserCourses(
        @RequestHeader("Authorization") String token
        ) {
        Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", ""));
        // 添加类型转换
        List<ProgressDTO> progressList = progressService.getUserCourseProgress(userId.intValue());
        System.out.println("返回数据：" + progressList);
        return ResponseEntity.ok(progressList);
    }

    @GetMapping("/check-enrollment/{courseId}")
    public ResponseEntity<Boolean> checkEnrollment(
        @PathVariable Integer courseId,
        @RequestHeader("Authorization") String token) {
        
        Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", ""));
        boolean isEnrolled = progressService.isUserEnrolled(userId, courseId);
        return ResponseEntity.ok(isEnrolled);
    }
    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> dropCourse(
        @PathVariable Integer courseId,
        @RequestHeader("Authorization") String token
    ) {
        try {
            Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", ""));
            progressService.deleteProgress(userId, courseId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                Map.of("error", "删除失败: " + e.getMessage())
            );
        }
    }


    @GetMapping("/timetable")
    public ResponseEntity<List<ChapterTimeDTO>> getUserTimetable(
        @RequestHeader("Authorization") String token) {
        Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", ""));
        List<ChapterTimeDTO> timetable = progressService.getUserTimetable(userId);
        return ResponseEntity.ok(timetable);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(
        @RequestHeader("Authorization") String token) {
        Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", ""));
        Map<String, Object> stats = progressService.getProgressStats(userId);
        return ResponseEntity.ok(stats);
    }
}