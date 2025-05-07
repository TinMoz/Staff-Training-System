package staff.training.system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import staff.training.system.model.dto.ChapterTimeDTO;
import staff.training.system.model.dto.ProgressDTO;
import staff.training.system.model.entity.LearningProgress;
import staff.training.system.repository.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;;

// 此服務類負責處理用戶的學習進度相關操作
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


    /// 保存已初始化的學習進度
    public void saveInitialProgress(LearningProgress progress) {
        progressRepository.save(progress);
    }
    /// 更新用戶的學習進度
    @Transactional
    public void updateProgress(Integer userId, Integer courseId, Integer progress) { 
        // 查找用戶的學習進度，如果不存在則創建一個新的進度實體
        LearningProgress  progressEntity = progressRepository.findByUser_IdAndCourse_Id(userId, courseId)
        .orElseGet(() -> {
            LearningProgress  newProgress = new LearningProgress (); // 創建新的學習進度實體
            newProgress.setUser(userRepository.findById(userId).orElseThrow()); // 通過userID設置User實體
            newProgress.setCourse(courseRepository.findById(courseId).orElseThrow()); // 通過courseID設置Course實體
            return newProgress;
        });
        // 更新學習進度和最後訪問時間
        progressEntity.setProgress(progress);
        progressEntity.setLastAccessed(new Date());
        progressRepository.save(progressEntity);
    }
    /// 獲取用戶的所有課程進度
    public List<ProgressDTO> getUserCourseProgress(Integer userId) { 
        return progressRepository.findUserProgress(userId);
    }
    /// 檢查用戶是否已經報名了某門課程
    public boolean isUserEnrolled(Integer userId, Integer courseId) {
        return progressRepository.findByUser_IdAndCourse_Id(userId, courseId).isPresent();
    }
    // 刪除當前學習進度
    @Transactional
    public void deleteProgress(Integer userId, Integer courseId) {
        // 先刪除關聯的報名請求
        enrollmentRequestRepository.deleteByUserAndCourse(userId, courseId);
        
        // 再刪除學習進度
        progressRepository.deleteByUserAndCourse(userId, courseId);
    }

    // 獲取用戶的課程時間表
    public List<ChapterTimeDTO> getUserTimetable(Integer userId) {
        // 獲取用戶的所有學習進度，並過濾掉已完成的課程
        return progressRepository.findByUser_Id(userId).stream()
            .filter(lp -> !(Boolean.TRUE.equals(lp.getCompleted()) && lp.getProgress() >= 100))
            // 將學習進度轉換為ChapterTimeDTO對象
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
    // 獲取用戶的學習進度信息
    public Map<String, Object> getProgressStats(Integer userId) {
        Map<String, Object> stats = new HashMap<>();
        // 獲取用戶的學習進度統計信息
        int enrolledCourses = progressRepository.countByUser_Id(userId);
        stats.put("enrolled", enrolledCourses);
        
        // 獲取用戶的已完成課程數量
        int completedChapters = progressRepository.countByUser_IdAndCompletedTrue(userId);
        stats.put("completedChapters", completedChapters);
        
        return stats;
    }

    // 獲取用戶的所有課程
    public List<staff.training.system.model.entity.Course> getUserCourses(Integer userId) {
        // 查詢用戶的所有學習進度，並提取課程信息
        return progressRepository.findByUser_Id(userId).stream()
            .map(LearningProgress::getCourse)
            .distinct() // 確保不重複
            .collect(Collectors.toList());
    }
}