package Staff.Training.System.Progress;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import jakarta.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;

import Staff.Training.System.ChapterTimeDTO;
import Staff.Training.System.Repository.*;;

@Service
public class ProgressService {
    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRequestRepository enrollmentRequestRepository;



    public void saveInitialProgress(LearningProgress progress) {
        progressRepository.save(progress);
    }

    @Transactional
    public void updateProgress(Integer userId, Integer courseId, Integer progress) { // 参数类型改为Integer
        LearningProgress  progressEntity = progressRepository.findByUser_IdAndCourse_Id(userId, courseId)
        .orElseGet(() -> {
            LearningProgress  newProgress = new LearningProgress ();
            newProgress.setUser(userRepository.findById(userId).orElseThrow()); // 通过ID获取User实体
            newProgress.setCourse(courseRepository.findById(courseId).orElseThrow()); // 通过ID获取Course实体
            return newProgress;
        });

        progressEntity.setProgress(progress);
        progressEntity.setLastAccessed(new Date());
        progressRepository.save(progressEntity);
    }

    public List<ProgressDTO> getUserCourseProgress(Integer userId) { // 参数类型改为Integer
        return progressRepository.findUserProgress(userId);
    }

    public boolean isUserEnrolled(Integer userId, Integer courseId) {
        return progressRepository.findByUser_IdAndCourse_Id(userId, courseId).isPresent();
    }

    @Transactional
    public void deleteProgress(Integer userId, Integer courseId) {
        // 先删除关联的enrollment_requests
        enrollmentRequestRepository.deleteByUserAndCourse(userId, courseId);
        
        // 再删除学习进度
        progressRepository.deleteByUserAndCourse(userId, courseId);
    }


    public List<ChapterTimeDTO> getUserTimetable(Integer userId) {
        return progressRepository.findByUser_Id(userId).stream()
            // 过滤掉已完成(completed=true)且进度为100%的课程
            .filter(lp -> !(Boolean.TRUE.equals(lp.getCompleted()) && lp.getProgress() >= 100))
            .map(lp -> new ChapterTimeDTO(
                lp.getCourse().getId(),
                lp.getCourse().getTitle(),
                lp.getChapter().getWeekday(),
                lp.getChapter().getStartTime(),
                lp.getChapter().getEndTime(),
                lp.getChapter().getTitle()
            ))
            .collect(Collectors.toList());
    }

    public Map<String, Object> getProgressStats(Integer userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 获取已选课程数量
        int enrolledCourses = progressRepository.countByUser_Id(userId);
        stats.put("enrolled", enrolledCourses);
        
        // 获取已完成章节数量
        int completedChapters = progressRepository.countByUser_IdAndCompletedTrue(userId);
        stats.put("completedChapters", completedChapters);
        
        return stats;
    }

    /**
     * 获取用户已报名的所有课程
     * @param userId 用户ID
     * @return 用户已报名的课程列表
     */
    public List<Staff.Training.System.Course> getUserCourses(Integer userId) {
        // 从学习进度中获取用户所有已报名的课程
        return progressRepository.findByUser_Id(userId).stream()
            .map(LearningProgress::getCourse)
            .distinct() // 确保不重复
            .collect(Collectors.toList());
    }
}