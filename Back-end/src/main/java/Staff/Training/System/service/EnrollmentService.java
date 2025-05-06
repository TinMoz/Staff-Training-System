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

@Service
public class EnrollmentService {
    @Autowired
    private EnrollmentRequestRepository requestRepository;
    @Autowired
    private ChapterRepository chapterRepository;
    @Autowired
    private ProgressService progressService;
    @Autowired
    private ProgressRepository progressRepository;

    public EnrollmentRequest createRequest(User user, Course course) {
        try {
            // 修改课程数量检查逻辑，不计算已完成的课程
            int enrolledActiveCoursesCount = progressRepository.countByUser_IdAndCompletedFalse(user.getId());
            if (enrolledActiveCoursesCount >= 6) {
                throw new RuntimeException("报名失败：您最多只能同时学习6个未完成的课程");
            }
            
            if (hasTimeConflict(user, course)) {
                throw new RuntimeException("时间冲突：该课程时间段与已有课程重叠");
            }
            
            // 修改检查逻辑：只有当存在待处理或已批准的请求时才阻止
            List<EnrollmentRequest.EnrollmentStatus> activeStatuses = List.of(
                EnrollmentRequest.EnrollmentStatus.PENDING,
                EnrollmentRequest.EnrollmentStatus.APPROVED
            );
            
            if (requestRepository.existsByUser_IdAndCourse_IdAndStatusIn(
                    user.getId(), course.getId(), activeStatuses)) {
                throw new RuntimeException("已有处理中或已批准的申请，无需重复提交");
            }

            EnrollmentRequest request = new EnrollmentRequest();
            request.setUser(user);
            request.setCourse(course);
            return requestRepository.save(request);
        } catch (Exception e) {
            // Add more detailed logging
            System.err.println("Enrollment error for course " + course.getId() + ": " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("创建课程请求失败: " + e.getMessage());
        }
    }

     public List<EnrollmentRequestDTO> getPendingRequests() {
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

    @Data
    @AllArgsConstructor
    public static class EnrollmentRequestDTO {
        private Integer id;
        private String username;
        private String courseTitle;
        private Date createdAt;
    }

    public void processRequest(Integer requestId, EnrollmentRequest.EnrollmentStatus status) {
        EnrollmentRequest request = requestRepository.findById(requestId).orElseThrow();
        request.setStatus(status);
        requestRepository.save(request);
        
        if (status == EnrollmentRequest.EnrollmentStatus.APPROVED) {
            initializeCourseProgress(request.getUser(), request.getCourse());
        }
    }

    private void initializeCourseProgress(User user, Course course) {
        try {
            // 获取课程的第一个章节
            Chapter firstChapter = chapterRepository.findFirstByCourseIdOrderByOrderNumAsc(course.getId())
                .orElseThrow(() -> new RuntimeException("课程没有可用章节"));
            
            // 创建初始学习进度
            LearningProgress progress = new LearningProgress();
            progress.setUser(user);
            progress.setCourse(course);
            progress.setChapter(firstChapter);  // Make sure this line is present!
            progress.setCompleted(false);
            progress.setProgress(0);
            progress.setLastAccessed(new Date());
            
            // Save the progress
            progressService.saveInitialProgress(progress);
        } catch (Exception e) {
            System.err.println("Progress initialization error: " + e.getMessage());
            throw e;  // Re-throw for proper handling
        }
    }

    // 修改时间冲突检测方法，考虑待处理的申请
    public boolean hasTimeConflict(User user, Course newCourse) {
        // 获取用户已报名的课程
        List<Course> userCourses = progressService.getUserCourses(user.getId());
        
        // 获取用户已有的待处理申请
        List<EnrollmentRequest> pendingRequests = requestRepository.findByUser_IdAndStatus(
            user.getId(), 
            EnrollmentRequest.EnrollmentStatus.PENDING
        );
        
        // 提取待处理申请的课程
        List<Course> pendingCourses = pendingRequests.stream()
            .map(EnrollmentRequest::getCourse)
            .collect(Collectors.toList());
        
        // 合并两个集合
        List<Course> allCourses = new ArrayList<>(userCourses);
        allCourses.addAll(pendingCourses);
        
        // 检查新课程与所有已有课程的时间冲突
        for (Course existingCourse : allCourses) {
            if (doCoursesHaveTimeConflict(existingCourse, newCourse)) {
                return true;
            }
        }
        
        return false;
    }

    // 辅助方法：检查两个课程之间是否有时间冲突
    private boolean doCoursesHaveTimeConflict(Course course1, Course course2) {
        // 获取两个课程的所有章节
        for (Chapter chapter1 : course1.getChapters()) {
            for (Chapter chapter2 : course2.getChapters()) {
                // 只有当周几相同时才检查时间冲突
                if (chapter1.getWeekday() == chapter2.getWeekday()) {
                    // 检查时间是否重叠
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

    // 辅助方法：检查两个时间段是否重叠
    private boolean doTimesOverlap(
        LocalTime start1, LocalTime end1, 
        LocalTime start2, LocalTime end2
    ) {
        return (start1.isBefore(end2) && end1.isAfter(start2));
    }

    @Data
    @AllArgsConstructor
    public static class TimeSlot {
        private Integer weekday;
        private LocalTime startTime;
        private LocalTime endTime;
    }

    
}
