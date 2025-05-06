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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {
    @Autowired
    private EnrollmentService enrollmentService;
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private ProgressRepository progressRepository;
    @Autowired
    private EnrollmentRequestRepository requestRepository;

    @PostMapping("/{courseId}")
    public ResponseEntity<?> applyCourse(@PathVariable Integer courseId,
                                         @RequestHeader("Authorization") String token) {
        try{ 
            Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", ""));
            User user = userRepository.findById(userId).orElseThrow();
            Course course = courseRepository.findById(courseId).orElseThrow();
            
            enrollmentService.createRequest(user, course);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
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

    @GetMapping("/pending")
    public ResponseEntity<List<EnrollmentService.EnrollmentRequestDTO>> getPendingRequests() {
      return ResponseEntity.ok(enrollmentService.getPendingRequests());
    }

    @GetMapping("/precheck/{courseId}")
    public ResponseEntity<?> precheckEnrollment(@PathVariable Integer courseId,
                                               @RequestHeader("Authorization") String token) {
        try {
            Integer userId = jwtUtils.getUserIDFromToken(token.replace("Bearer ", ""));
            User user = userRepository.findById(userId).orElseThrow();
            Course course = courseRepository.findById(courseId).orElseThrow();
            
            // 修改课程数量检查逻辑，只计算未完成的课程数量
            int activeCoursesCount = progressRepository.countByUser_IdAndCompletedFalse(userId);
            int pendingRequestsCount = requestRepository.countByUser_IdAndStatus(userId, EnrollmentRequest.EnrollmentStatus.PENDING);
            int totalActiveCoursesCount = activeCoursesCount + pendingRequestsCount;
            
            if (totalActiveCoursesCount >= 6) {
                return ResponseEntity.badRequest().body(Map.of("error", "最多只能同时学习6个未完成的课程"));
            }
            
            // 检查时间冲突
            if (enrollmentService.hasTimeConflict(user, course)) {
                return ResponseEntity.badRequest().body(Map.of("error", "时间冲突：该课程时间段与已有课程或待处理申请重叠"));
            }
            
            // 检查是否已申请该课程
            List<EnrollmentRequest.EnrollmentStatus> activeStatuses = List.of(
                EnrollmentRequest.EnrollmentStatus.PENDING,
                EnrollmentRequest.EnrollmentStatus.APPROVED
            );
            
            if (requestRepository.existsByUser_IdAndCourse_IdAndStatusIn(userId, courseId, activeStatuses)) {
                return ResponseEntity.badRequest().body(Map.of("error", "已提交过该课程的申请"));
            }
            
            // 所有检查都通过
            return ResponseEntity.ok(Map.of("canEnroll", true));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "检查失败: " + e.getMessage()));
        }
    }

    @PutMapping("/{requestId}/{status}")
    @Transactional  
    public ResponseEntity<Void> processRequest(@PathVariable Integer requestId,
                                              @PathVariable String status) {
        EnrollmentRequest.EnrollmentStatus statusEnum = 
            EnrollmentRequest.EnrollmentStatus.valueOf(status.toUpperCase());
        enrollmentService.processRequest(requestId, statusEnum);
        return ResponseEntity.ok().build();
    }
}