package staff.training.system.controller;


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
import lombok.Data;
import staff.training.system.model.entity.Course;
import staff.training.system.model.entity.User;
import staff.training.system.model.entity.Chapter;
import staff.training.system.model.entity.Course.CourseDetailDTO;
import staff.training.system.repository.CourseRepository;
import staff.training.system.repository.UserRepository;

// 此Controller提供了課程的增刪改查等操作，處理Course請求
@RestController // 返回Json格式的響應
@RequestMapping("/api/courses") // 設置請求路徑為/api/courses
public class CourseController {
    @Autowired
    private CourseRepository courseRepository;// 課程數據庫操作接口
    @Autowired
    private UserRepository userRepository;// 用戶數據庫操作接口

    @GetMapping // 獲取所有課程
    public ResponseEntity<List<CourseDetailDTO>> getAllCourses() {
        List<Course> courses = courseRepository.findAll(); // 獲取所有課程信息存入到courses列表中
        List<CourseDetailDTO> courseDetails = courses.stream() // 使用Stream API將Course實體映射為CourseDetailDTO數據傳輸對象，便於向前端返回所需數據結構
        .map(course -> new CourseDetailDTO(
            course.getId(), // 取得課程ID
            course.getTitle(), // 取得課程標題
            course.getCourseCode(), // 取得課程代碼
            course.getCredits(), // 取得學分
            course.getChapters(), // 取得課程章節
            course.getDescription(), // 取得課程描述
            course.getCreatedAt(),     // 取得創建時間
            course.getTeacher() // 取得授課教師名字
        ))
        .toList(); // 轉換為列表形式
        return ResponseEntity.ok(courseDetails);
    }
    @GetMapping("/{id}") // 根據ID獲取課程詳情
    public ResponseEntity<Course.CourseDetailDTO> getCourseById(@PathVariable Integer id) {
        Course course = courseRepository.findByIdWithChapters(id).orElse(null); // 根據ID查詢課程信息
        
        if (course == null) { // 如果課程不存在，返回404 Not Found響應
            return ResponseEntity.notFound().build();
        }

        Hibernate.initialize(course.getChapters()); // 初始化強制加載懶加載的章节数据
        
        course.getChapters().sort(Comparator.comparing(Chapter::getOrderNum));//根據ordernum排序章節

        Course.CourseDetailDTO courseDetail = new Course.CourseDetailDTO( // 將Course實體映射為CourseDetailDTO數據傳輸對象，便於向前端返回所需數據結構
            course.getId(),// 取得課程ID
            course.getTitle(), // 取得課程標題
            course.getCourseCode(), // 取得課程代碼
            course.getCredits(), // 取得學分
            course.getChapters(), // 取得課程章節
            course.getDescription(), // 取得課程描述
            course.getCreatedAt(), // 取得創建時間
            course.getTeacher() // 取得授課教師名字
        );

        return ResponseEntity.ok(courseDetail);
    }

    @PostMapping //處理Post請求
    @PreAuthorize("hasRole('ADMIN')") // 只有Admin權限的用戶可以訪問
    public ResponseEntity<Course> createCourse(
        @RequestBody CourseCreateRequest request, //接收並解析來至客戶端的Json
        Authentication authentication //當前已認證的用戶信息
    ) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal(); //從認證對象中獲取用戶詳細信息
        User creator = userRepository.findByUsername(userDetails.getUsername()).orElseThrow(); //根據用戶名從數據庫查找完整的用戶entity
    
        Course course = new Course();
        course.setTitle(request.getTitle()); // 透過Json的名字設置課程名字
        course.setCourseCode(request.getCourseCode()); // 透過Json的課程代碼設置課程代碼
        course.setDescription(request.getDescription()); // 透過Json的課程描述設置課程描述
        course.setCredits(request.getCredits()); // 透過Json的學分設置課程學分 
        course.setCreatedBy(creator); // 設置創建者
        course.setTeacher(request.getTeacher());  // 透過Json的教師名字設置課程教師名字
        course.setChapters(request.getChapters().stream() // 透過Stream 將ChapterDTO轉換為Chapter實體
            .map(dto -> {
                Chapter ch = new Chapter();
                ch.setCourse(course); // 設置課程ID
                ch.setTitle(dto.getTitle()); // 透過Json的章節名字設置課程章節名字
                ch.setContent(dto.getContent()); // 透過Json的章節內容設置課程章節內容
                ch.setOrderNum(dto.getOrderNum()); // 透過Json的章節順序設置課程章節順序
                ch.setDuration(dto.getDuration()); // 透過Json的章節時長設置課程章節時長
                
                return ch;    
            }).toList()
        );
    
        return ResponseEntity.ok(courseRepository.save(course));
    }

    // 更新課程信息
    @PutMapping("/{id}") //處理Put請求
    @PreAuthorize("hasRole('ADMIN')") // 只有Admin權限的用戶可以訪問
    public ResponseEntity<Course> updateCourse(
        @PathVariable Integer id, //從路徑中獲取課程ID
        @RequestBody CourseCreateRequest request //接收並解析來至客戶端的Json
    ) {
        Course existingCourse = courseRepository.findById(id).orElseThrow(); // 根據ID查詢課程信息
        
        existingCourse.setTitle(request.getTitle()); // 透過Json的名字設置課程名字
        existingCourse.setCourseCode(request.getCourseCode()); // 透過Json的課程代碼設置課程代碼
        existingCourse.setDescription(request.getDescription()); // 透過Json的課程描述設置課程描述
        existingCourse.setCredits(request.getCredits());    // 透過Json的學分設置課程學分
        existingCourse.setTeacher(request.getTeacher());  // 透過Json的教師名字設置課程教師名字
        
        // 清空原有的章節並添加新的章節
        existingCourse.getChapters().clear(); // 清空原有的章節
        existingCourse.getChapters().addAll( // 透過Json的章節設置課程章節
            request.getChapters().stream().map(dto -> { // 透過Stream 將ChapterDTO轉換為Chapter實體
                Chapter ch = new Chapter(); // 創建新的Chapter實體
                ch.setTitle(dto.getTitle()); // 透過Json的章節名字設置課程章節名字
                ch.setContent(dto.getContent()); // 透過Json的章節內容設置課程章節內容
                ch.setOrderNum(dto.getOrderNum()); // 透過Json的章節順序設置課程章節順序
                ch.setDuration(dto.getDuration()); // 透過Json的章節時長設置課程章節時長
                ch.setCourse(existingCourse);  // 設置課程ID      
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


