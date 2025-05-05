package Staff.Training.System;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import Staff.Training.System.Progress.ProgressRepository;
import Staff.Training.System.Repository.CourseRepository;
import Staff.Training.System.Repository.EnrollmentRequestRepository;
import Staff.Training.System.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;

import org.springframework.security.crypto.password.PasswordEncoder;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')") // 全局要求 ADMIN 角色
public class AdminController {
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private EnrollmentRequestRepository enrollmentRequestRepository;

    public AdminController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    // 删除课程（管理员专属接口）
    @DeleteMapping("/courses/{id}")
    @Transactional // 添加事务注解
    public ResponseEntity<?> deleteCourse(@PathVariable Integer id) {
        try {
            Course course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("课程不存在"));

            // 1. 先删除学习进度（最深层依赖）
            progressRepository.deleteByCourseId(course.getId());

            // 2. 删除关联的EnrollmentRequest
            enrollmentRequestRepository.deleteAll(course.getEnrollmentRequests());

            // 3. 最后删除课程（级联删除章节）
            courseRepository.delete(course);

            return ResponseEntity.ok("课程删除成功");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("删除失败: " + e.getMessage());
        }
    }

    @PostConstruct
    public void initAdminUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(role.ADMIN);
            admin.setEmail("admin@example.com"); // 添加 email
            userRepository.save(admin);
        }
    }

    // 其他管理员接口...
}