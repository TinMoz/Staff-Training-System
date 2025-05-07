package staff.training.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.transaction.Transactional;
import staff.training.system.model.entity.Course;
import staff.training.system.model.entity.EnrollmentRequest;
import staff.training.system.model.entity.User;
import staff.training.system.repository.CourseRepository;
import staff.training.system.repository.EnrollmentRequestRepository;
import staff.training.system.repository.ProgressRepository;
import staff.training.system.repository.UserRepository;
import staff.training.system.security.JwtUtils;
import staff.training.system.service.EnrollmentService;
import staff.training.system.service.ProgressService;

import java.util.List;
import java.util.Map;

// 此Controller提供了課程報名的相關操作，處理Enrollment請求
@RestController // 返回Json格式的響應
@RequestMapping("/api/enrollment") // 設置請求路徑為/api/enrollment
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService; // 報名服務接口
    @Autowired
    private CourseRepository courseRepository; // 課程數據庫操作接口
    @Autowired
    private UserRepository userRepository; // 用戶數據庫操作接口
    @Autowired
    private ProgressRepository progressRepository; // 學習進度數據庫操作接口
    @Autowired
    private EnrollmentRequestRepository requestRepository; // 報名請求數據庫操作接口
    @Autowired
    private ProgressService progressService; // 學習進度服務接口
    @Autowired
    private JwtUtils jwtUtils; // JWT工具類，用於解析JWT令牌
    

    // 用戶報名課程的請求處理方法
    @PostMapping("/{courseId}")
    public ResponseEntity<?> applyCourse(@PathVariable Integer courseId,
                                         @RequestHeader("Authorization") String token) {
        try{ 
            Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", "")); // 解析JWT令牌獲取用戶ID
            User user = userRepository.findById(userId).orElseThrow(); // 根據用戶ID查詢用戶信息
            Course course = courseRepository.findById(courseId).orElseThrow(); // 根據課程ID查詢課程信息
            
            enrollmentService.createRequest(user, course); // 調用報名服務創建報名請求
            return ResponseEntity.ok().build();
        } catch (Exception e) { // 捕獲異常
            String message;
            if (e.getMessage().contains("时间冲突")) {
                message = "课程时间冲突，无法报名";
            } else if (e.getMessage().contains("最多只能报名6个课程")) {
                message = "您最多只能报名6个课程，请退选部分课程后再试";
            } else {
                message = "提交失败";
            }
            return ResponseEntity.badRequest().body(Map.of("error", message));
        }
    }
    // 獲取用戶的所有報名請求
    @GetMapping("/pending") // 獲取所有待處理的報名請求
    public ResponseEntity<List<EnrollmentService.EnrollmentRequestDTO>> getPendingRequests() {
      return ResponseEntity.ok(enrollmentService.getPendingRequests());
    }

    // 獲取用戶的所有報名請求
    @GetMapping("/precheck/{courseId}")// 獲取用戶的報名請求
    public ResponseEntity<?> precheckEnrollment(@PathVariable Integer courseId,
                                               @RequestHeader("Authorization") String token) {
        try {
            Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", "")); // 解析JWT令牌獲取用戶ID
            User user = userRepository.findById(userId).orElseThrow(); // 根據用戶ID查詢用戶信息
            Course course = courseRepository.findById(courseId).orElseThrow(); // 根據課程ID查詢課程信息
            // 計算用戶當前以啟動的課程數量
            int activeCoursesCount = progressRepository.countByUser_IdAndCompletedFalse(userId); 
            // 計算用戶當前待處理的報名請求數量
            int pendingRequestsCount = requestRepository.countByUser_IdAndStatus(userId, EnrollmentRequest.EnrollmentStatus.PENDING);
            // 計算用戶當前的報名請求數量
            int totalActiveCoursesCount = activeCoursesCount + pendingRequestsCount;
            // 如果用戶當前的報名請求數量大於等於6，則返回錯誤響應
            if (totalActiveCoursesCount >= 6) {
                return ResponseEntity.badRequest().body(Map.of("error", "最多只能同时学习6个未完成的课程"));
            }
            
            // 檢查課程時間是否衝突，返回錯誤響應
            if (enrollmentService.hasTimeConflict(user, course)) {
                return ResponseEntity.badRequest().body(Map.of("error", "时间冲突：该课程时间段与已有课程或待处理申请重叠"));
            }
            
            // 定義活動狀態的列表
            List<EnrollmentRequest.EnrollmentStatus> activeStatuses = List.of(
                EnrollmentRequest.EnrollmentStatus.PENDING,
                EnrollmentRequest.EnrollmentStatus.APPROVED
            );
            // 檢查用戶是否已經提交過該課程的報名請求，返回錯誤響應
            if (requestRepository.existsByUser_IdAndCourse_IdAndStatusIn(userId, courseId, activeStatuses)) {
                return ResponseEntity.badRequest().body(Map.of("error", "已提交过该课程的申请"));
            }
            
            
            return ResponseEntity.ok(Map.of("canEnroll", true));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "检查失败: " + e.getMessage()));
        }
    }
    // 允許管理員審批課程報名申請
    @PutMapping("/{requestId}/{status}") // 更新報名請求的狀態
    @Transactional  // 事務性操作，確保數據一致性
    public ResponseEntity<Void> processRequest(@PathVariable Integer requestId,
                                              @PathVariable String status) {
        EnrollmentRequest.EnrollmentStatus statusEnum = 
            EnrollmentRequest.EnrollmentStatus.valueOf(status.toUpperCase());// 將字符串狀態轉換為枚舉類型
        enrollmentService.processRequest(requestId, statusEnum); // 調用報名服務處理報名請求
        return ResponseEntity.ok().build();
    }
    // 检查用戶是否已報名課程
    @GetMapping("/check/{courseId}")
    public ResponseEntity<Boolean> checkEnrollment(
        @PathVariable Integer courseId,
        @RequestHeader("Authorization") String token) {
        // 解析JWT令牌獲取用戶ID
        Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", ""));
        // 調用學習進度服務檢查用戶是否已報名課程
        boolean isEnrolled = progressService.isUserEnrolled(userId, courseId);
        return ResponseEntity.ok(isEnrolled);
    }
}