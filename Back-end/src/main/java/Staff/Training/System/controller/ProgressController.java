package staff.training.system.controller;

import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import staff.training.system.model.dto.ChapterTimeDTO;
import staff.training.system.model.dto.ProgressDTO;
import staff.training.system.model.dto.ProgressRequestDTO;
import staff.training.system.security.JwtUtils;
import staff.training.system.service.ProgressService;



// 此Controller提供了學習進度的相關操作，處理Progress請求
@RestController // 返回Json格式的響應
@RequestMapping("/api/progress") // 設置請求路徑為/api/progress

public class ProgressController{
    @Autowired
    private ProgressService progressService; // 學習進度服務接口
    @Autowired
    private JwtUtils jwtUtils; // JWT工具類，用於解析JWT令牌
    
    //更新學習進度
    @PutMapping
    public ResponseEntity<Void> updateProgress(
        @RequestBody ProgressRequestDTO request, // 接收前端傳遞的學習進度請求對象
        @RequestHeader("Authorization") String token) { // 獲取請求頭中的JWT令牌
        // 解析JWT令牌獲取用戶ID
        Integer userID = jwtUtils.getUserIDFromToken(token.replace("Bearer ", "")); 
        // 調用學習進度服務更新學習進度
        progressService.updateProgress(userID.intValue(), request.getCourseId().intValue(), request.getProgress());
        return ResponseEntity.ok().build();
    }
    // 獲取用戶的所有課程進度
    @GetMapping("/courses")
    public ResponseEntity<List<ProgressDTO>> getUserCourses(
        @RequestHeader("Authorization") String token
        ) {
        // 解析JWT令牌獲取用戶ID
        Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", ""));
        // 調用學習進度服務獲取用戶的所有課程進度
        List<ProgressDTO> progressList = progressService.getUserCourseProgress(userId.intValue());
        System.out.println("返回数据：" + progressList);
        return ResponseEntity.ok(progressList);
    }

    // 處理退課
    @DeleteMapping("/{courseId}")
    public ResponseEntity<?> dropCourse(
        @PathVariable Integer courseId,
        @RequestHeader("Authorization") String token
    ) {
        try {
            // 解析JWT令牌獲取用戶ID
            Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", ""));
            // 調用學習進度服務刪除學習進度
            progressService.deleteProgress(userId, courseId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                Map.of("error", "删除失败: " + e.getMessage())
            );
        }
    }

    // 獲取用戶的學習時間表
    @GetMapping("/timetable")
    public ResponseEntity<List<ChapterTimeDTO>> getUserTimetable(
        @RequestHeader("Authorization") String token) {
        // 解析JWT令牌獲取用戶ID
        Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", ""));
        // 調用學習進度服務獲取用戶的學習時間表
        List<ChapterTimeDTO> timetable = progressService.getUserTimetable(userId);
        return ResponseEntity.ok(timetable);
    }

    // 獲取用戶的學習進度數據
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(
        @RequestHeader("Authorization") String token) {
        // 解析JWT令牌獲取用戶ID
        Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", ""));
        // 調用學習進度服務獲取用戶的學習進度統計數據
        Map<String, Object> stats = progressService.getProgressStats(userId);
        return ResponseEntity.ok(stats);
    }
}