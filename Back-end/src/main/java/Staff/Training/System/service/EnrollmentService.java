package staff.training.system.service;

import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalTime;
import java.util.Date;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import staff.training.system.model.entity.Course;
import staff.training.system.model.entity.EnrollmentRequest;
import staff.training.system.model.entity.LearningProgress;
import staff.training.system.model.entity.User;
import staff.training.system.model.entity.Chapter;
import staff.training.system.repository.*;

// 此服務類負責處理用戶的課程報名請求和進度初始化的邏輯
@Service
public class EnrollmentService {
    @Autowired
    private EnrollmentRequestRepository requestRepository; // 報名請求數據庫操作接口
    @Autowired
    private ChapterRepository chapterRepository; // 課程章節數據庫操作接口
    @Autowired
    private ProgressService progressService; // 學習進度服務接口
    @Autowired
    private ProgressRepository progressRepository; // 學習進度數據庫操作接口
    // 創建報名請求
    public EnrollmentRequest createRequest(User user, Course course) {
        try {
            // 計算用戶是否已經報名超過或等於6個課程
            int enrolledActiveCoursesCount = progressRepository.countByUser_IdAndCompletedFalse(user.getId());
            if (enrolledActiveCoursesCount > 6) {
                throw new RuntimeException("报名失败：您最多只能同时学习6个未完成的课程");
            }
            // 检查时间冲突
            if (hasTimeConflict(user, course)) {
                throw new RuntimeException("时间冲突：该课程时间段与已有课程重叠");
            }
            
            // 創建一個不可修改的報名狀態清單並命名為activeStatuses
            List<EnrollmentRequest.EnrollmentStatus> activeStatuses = List.of(
                EnrollmentRequest.EnrollmentStatus.PENDING,
                EnrollmentRequest.EnrollmentStatus.APPROVED
            );
            // 檢查用戶是否已經有待處理的報名請求
            if (requestRepository.existsByUser_IdAndCourse_IdAndStatusIn(
                    user.getId(), course.getId(), activeStatuses)) {
                throw new RuntimeException("已有处理中或已批准的申请，无需重复提交");
            }
            // 如果檢查全通過則創建報名請求
            EnrollmentRequest request = new EnrollmentRequest();
            request.setUser(user); // 設置用戶
            request.setCourse(course); // 設置課程
            return requestRepository.save(request); // 保存報名請求到Enrollment_Request數據庫
        } catch (Exception e) {
            System.err.println("Enrollment error for course " + course.getId() + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("创建课程请求失败: " + e.getMessage());
        }
    }
    // 獲取所有待處理的報名請求
    public List<EnrollmentRequestDTO> getPendingRequests() {
        // 查詢所有狀態為PENDING的報名請求並轉換為DTO列表
        return requestRepository.findByStatus(EnrollmentRequest.EnrollmentStatus.PENDING)
            .stream()
            .map(er -> new EnrollmentRequestDTO(
                er.getId(),
                er.getUser().getUsername(),
                er.getCourse().getTitle(),
                er.getCreatedAt()
            ))
            .collect(Collectors.toList());
    }

    // 處理報名請求
    public void processRequest(Integer requestId, EnrollmentRequest.EnrollmentStatus status) {
        // 查詢報名請求
        EnrollmentRequest request = requestRepository.findById(requestId).orElseThrow();
        // 檢查報名請求的狀態是否為PENDING
        request.setStatus(status);
        // 更新報名請求的狀態
        requestRepository.save(request);
        // 如果報名請求被批准，則初始化學習進度
        if (status == EnrollmentRequest.EnrollmentStatus.APPROVED) {
            initializeCourseProgress(request.getUser(), request.getCourse());
        }
    }
    // 初始化學習進度
    private void initializeCourseProgress(User user, Course course) {
        try {
            // 獲取課程的第一個章節
            Chapter firstChapter = chapterRepository.findFirstByCourseIdOrderByOrderNumAsc(course.getId())
                .orElseThrow(() -> new RuntimeException("课程没有可用章节"));
            
            // 創建學習進度實體
            LearningProgress progress = new LearningProgress();
            progress.setUser(user); // 設置用戶為報名用戶
            progress.setCourse(course); // 設置課程為當前報名課程
            progress.setChapter(firstChapter);  // 設置章節為第一章節
            progress.setCompleted(false); // 設置完成狀態為否
            progress.setProgress(0); // 設置進度為0
            progress.setLastAccessed(new Date()); // 設置最後訪問時間為當前時間
            
            // 保存學習進度到數據庫
            progressService.saveInitialProgress(progress);
        } catch (Exception e) {
            System.err.println("Progress initialization error: " + e.getMessage());
            throw e;  // Re-throw for proper handling
        }
    }

    // 檢查時間衝突
    public boolean hasTimeConflict(User user, Course newCourse) {
        // 獲取用戶已報名的課程
        List<Course> userCourses = progressService.getUserCourses(user.getId()).stream()
        .filter(course -> {
            // 檢查用戶的課程是否未完成，如果已經完成則不檢查時間衝突，只返回未完成的課程
            return progressRepository.findByUser_IdAndCourse_IdAndCompletedFalse(
                user.getId(), course.getId()).isPresent();
        })
        .collect(Collectors.toList());
        
        // 獲取用戶的所有待處理報名請求
        List<EnrollmentRequest> pendingRequests = requestRepository.findByUser_IdAndStatus(
            user.getId(),
            EnrollmentRequest.EnrollmentStatus.PENDING
        );
        
        // 獲取所有待處理報名請求的課程
        List<Course> pendingCourses = pendingRequests.stream()
            .map(EnrollmentRequest::getCourse)
            .collect(Collectors.toList());
        
        // 將用戶已報名的課程和待處理報名請求的課程合併
        List<Course> allCourses = new ArrayList<>(userCourses);
        allCourses.addAll(pendingCourses);
        
        // 遍歷所有課程，檢查時間衝突
        for (Course existingCourse : allCourses) {
            if (doCoursesHaveTimeConflict(existingCourse, newCourse)) {
                return true;
            }
        }
        
        return false;
    }

    // 檢查兩個課程是否有時間衝突(用於輔助hasTimeConflict進行時間檢查)
    private boolean doCoursesHaveTimeConflict(Course course1, Course course2) {
        // 獲取兩個課程的所有章節
        for (Chapter chapter1 : course1.getChapters()) {
            for (Chapter chapter2 : course2.getChapters()) {
                // 只有當周幾相同時才檢查時間衝突
                if (chapter1.getWeekday() == chapter2.getWeekday()) {
                    // 檢查時間段是否重疊
                    if (doTimesOverlap(
                        LocalTime.parse(chapter1.getStartTime()), LocalTime.parse(chapter1.getEndTime()),
                        LocalTime.parse(chapter2.getStartTime()), LocalTime.parse(chapter2.getEndTime())
                    )) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    // 檢查兩個時間段是否重疊 start1在end2前且end1在start2後=重疊
    private boolean doTimesOverlap(
        LocalTime start1, LocalTime end1, 
        LocalTime start2, LocalTime end2
    ) {
        return (start1.isBefore(end2) && end1.isAfter(start2));
    }
    // 時間段DTO類
    @Data
    @AllArgsConstructor
    public static class TimeSlot {
        private Integer weekday;
        private LocalTime startTime;
        private LocalTime endTime;
    }
    // 報名請求DTO類
    @Data
    @AllArgsConstructor
    public static class EnrollmentRequestDTO {
        private Integer id;
        private String username;
        private String courseTitle;
        private Date createdAt;
    }

    
}
