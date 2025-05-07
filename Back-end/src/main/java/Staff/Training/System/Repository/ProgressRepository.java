package staff.training.system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import staff.training.system.model.dto.ProgressDTO;
import staff.training.system.model.entity.LearningProgress;
import java.util.List;
import java.util.Optional;

//學習進度倉庫接口
@Repository
public interface ProgressRepository extends JpaRepository<LearningProgress , Integer> {
    // 根據用戶ID和課程ID查詢學習進度
    Optional<LearningProgress > findByUser_IdAndCourse_Id(Integer userId, Integer courseId);

    // 查詢特定用戶的學習進度記錄 並組織成ProgressDTO
    @Query("SELECT NEW staff.training.system.model.dto.ProgressDTO(" +
       "c.id, c.title, ch.title, lp.progress, lp.lastAccessed) " + 
       "FROM LearningProgress lp " +
       "JOIN lp.course c " +
       "JOIN lp.chapter ch " +
       "WHERE lp.user.id = :userId")
    List<ProgressDTO> findUserProgress(@Param("userId") Integer userId);

    // 根據用戶ID和課程ID刪除學習進度
    @Modifying
    @Query("DELETE FROM LearningProgress lp WHERE lp.user.id = :userId AND lp.course.id = :courseId")
    void deleteByUserAndCourse(@Param("userId") Integer userId, @Param("courseId") Integer courseId);
    // 根據用戶ID刪除學習進度
    @Modifying
    @Query("DELETE FROM LearningProgress lp WHERE lp.course.id = :courseId")
    void deleteByCourseId(@Param("courseId") Integer courseId);

    // 根據用戶ID查詢學習進度 使用JoinFetch優化查詢次數
    @Query("SELECT lp FROM LearningProgress lp JOIN FETCH lp.chapter WHERE lp.user.id = :userId")
    List<LearningProgress> findByUser_Id(@Param("userId") Integer userId);

    // 根據用戶ID計算用戶的的課程數量
    int countByUser_Id(Integer userId);

    // 根據用戶ID和CompletedTrue的數量計算用戶已完成的課程數量
    int countByUser_IdAndCompletedTrue(Integer userId);

    // 根據用戶ID和CompletedFalse的數量計算用戶尚未完成的課程數量
    int countByUser_IdAndCompletedFalse(Integer userId);
    // 根據用戶ID和課程ID查詢未完成的課程
    Optional<LearningProgress> findByUser_IdAndCourse_IdAndCompletedFalse(Integer userId, Integer courseId);
}