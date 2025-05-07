package staff.training.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import staff.training.system.model.entity.Course;
import staff.training.system.model.entity.User; 
import staff.training.system.model.entity.Role;
import staff.training.system.repository.CourseRepository;
import staff.training.system.repository.EnrollmentRequestRepository;
import staff.training.system.repository.ProgressRepository;
import staff.training.system.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

//此Controller提供了管理員專屬的API接口，主要用於課程的刪除及生產預設Admin的操作
@RestController
@RequestMapping("/api/admin")// 管理員專屬接口
@PreAuthorize("hasRole('ADMIN')") // 只有Admin權限的用戶可以訪問
public class AdminController {
    private final PasswordEncoder passwordEncoder; // 密碼加密器

    @Autowired
    private CourseRepository courseRepository; // 課程數據庫操作接口

    @Autowired
    private UserRepository userRepository; // 用戶數據庫操作接口
    
    @Autowired
    private ProgressRepository progressRepository; // 學習進度數據庫操作接口

    @Autowired
    private EnrollmentRequestRepository enrollmentRequestRepository; // 報名請求數據庫操作接口

    public AdminController(PasswordEncoder passwordEncoder) { // 密碼加密器的注入
        this.passwordEncoder = passwordEncoder;
    }

    // 删除课程
    @DeleteMapping("/courses/{id}")
    @Transactional//事務註解 確保所有操作在同一事務中執行
    public ResponseEntity<?> deleteCourse(@PathVariable Integer id) {
        try {
            Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

            // 刪除學習進度
            progressRepository.deleteByCourseId(course.getId());

            // 刪除關聯報名請求
            enrollmentRequestRepository.deleteAll(course.getEnrollmentRequests());

            // 最后删除课程
            courseRepository.delete(course);

            return ResponseEntity.ok("课程删除成功");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("删除失败: " + e.getMessage());
        }
    }
    //initAdminUser確保始終存在一個Admin用戶並初始化其數據
    @PostConstruct // Spring啟動後自動調用此方法
    public void initAdminUser() { 
        // 檢查是否存在Username為admin的用戶
        if (!userRepository.existsByUsername("admin")) {
            // 如果不存在，創建一個新的Admin用戶
            User admin = new User();
            admin.setUsername("admin"); // 管理員名字
            admin.setPassword(passwordEncoder.encode("admin123")); //管理員密碼
            admin.setRole(Role.ADMIN); // 管理員權限
            admin.setEmail("admin@example.com"); // 設置電郵
            userRepository.save(admin); // 保存admin賬戶到數據庫
        }
    }
}