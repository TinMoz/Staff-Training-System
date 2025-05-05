package Staff.Training.System;


import java.util.Comparator;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import Staff.Training.System.Course.CourseDetailDTO;
import Staff.Training.System.Repository.CourseRepository;
import Staff.Training.System.Repository.UserRepository;
import lombok.Data;


@RestController
@RequestMapping("/api/courses")
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<CourseDetailDTO>> getAllCourses() {
        List<Course> courses = courseRepository.findAll();
        List<CourseDetailDTO> courseDetails = courses.stream()
        .map(course -> new CourseDetailDTO(
            course.getId(),
            course.getTitle(),
            course.getCourseCode(),
            course.getCredits(),
            course.getChapters(),
            course.getDescription(), // 新增
            course.getCreatedAt(),     // 确保包含所需字段
            course.getTeacher()
        ))
        .toList();
        return ResponseEntity.ok(courseDetails);
    }
    @GetMapping("/{id}")
    public ResponseEntity<Course.CourseDetailDTO> getCourseById(@PathVariable Integer id) {
        // 使用JOIN FETCH强制加载章节数据
        Course course = courseRepository.findByIdWithChapters(id).orElse(null);
        
        if (course == null) {
            return ResponseEntity.notFound().build();
        }

        // 强制初始化懒加载数据
        Hibernate.initialize(course.getChapters());
        
        // 按orderNum排序
        course.getChapters().sort(Comparator.comparing(chapter::getOrderNum));

        // 构造DTO
        Course.CourseDetailDTO courseDetail = new Course.CourseDetailDTO(
            course.getId(),
            course.getTitle(),
            course.getCourseCode(),
            course.getCredits(),
            course.getChapters(),
            course.getDescription(),
            course.getCreatedAt(),
            course.getTeacher()
        );

        return ResponseEntity.ok(courseDetail);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> createCourse(
        @RequestBody CourseCreateRequest request, // 改用DTO接收
        Authentication authentication
    ) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User creator = userRepository.findByUsername(userDetails.getUsername()).orElseThrow();
    
        Course course = new Course();
        course.setTitle(request.getTitle());
        course.setCourseCode(request.getCourseCode());
        course.setDescription(request.getDescription());
        course.setCredits(request.getCredits());
        course.setCreatedBy(creator);
        course.setTeacher(request.getTeacher());  // 设置 teacher 字段
        course.setChapters(request.getChapters().stream()
            .map(dto -> {
                chapter ch = new chapter();
                ch.setCourse(course);
                ch.setTitle(dto.getTitle());
                ch.setContent(dto.getContent());
                ch.setOrderNum(dto.getOrderNum());
                ch.setDuration(dto.getDuration());
                
                return ch;    
            }).toList()
        );
    
        return ResponseEntity.ok(courseRepository.save(course));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Course> updateCourse(
        @PathVariable Integer id,
        @RequestBody CourseCreateRequest request
    ) {
        Course existingCourse = courseRepository.findById(id).orElseThrow();
        
        existingCourse.setTitle(request.getTitle());
        existingCourse.setCourseCode(request.getCourseCode());
        existingCourse.setDescription(request.getDescription());
        existingCourse.setCredits(request.getCredits());
        existingCourse.setTeacher(request.getTeacher()); 
        
        // 更新章节逻辑（需根据需求实现）
        existingCourse.getChapters().clear();
        existingCourse.getChapters().addAll(
            request.getChapters().stream().map(dto -> {
                chapter ch = new chapter();
                ch.setTitle(dto.getTitle());
                ch.setContent(dto.getContent());
                ch.setOrderNum(dto.getOrderNum());
                ch.setDuration(dto.getDuration());
                ch.setCourse(existingCourse);       
                return ch;
            }).toList()
        );
        
        return ResponseEntity.ok(courseRepository.save(existingCourse));
    }



    @Data
    public static class CourseCreateRequest {
        private String title;
        private String courseCode;
        private String description;
        private int credits;
        private List<ChapterDTO> chapters;
        private String teacher; // 新增 teacher 字段

    
        @Data
        public static class ChapterDTO {
            private String title;
            private String content;
            private Integer orderNum;
            private Integer duration;
        }
    }
    
}


