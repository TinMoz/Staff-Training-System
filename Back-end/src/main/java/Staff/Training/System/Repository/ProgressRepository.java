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

@Repository
public interface ProgressRepository extends JpaRepository<LearningProgress , Integer> {
    // 修正方法名为关联实体属性路径
    Optional<LearningProgress > findByUser_IdAndCourse_Id(Integer userId, Integer courseId);

    // 修正JPQL查询中的属性路径
    @Query("SELECT NEW staff.training.system.model.dto.ProgressDTO(" +
       "c.id, c.title, ch.title, lp.progress, lp.lastAccessed) " +  // 添加 c.id
       "FROM LearningProgress lp " +
       "JOIN lp.course c " +
       "JOIN lp.chapter ch " +
       "WHERE lp.user.id = :userId")
    List<ProgressDTO> findUserProgress(@Param("userId") Integer userId);


    @Modifying
    @Query("DELETE FROM LearningProgress lp WHERE lp.user.id = :userId AND lp.course.id = :courseId")
    void deleteByUserAndCourse(@Param("userId") Integer userId, @Param("courseId") Integer courseId);

    @Modifying
    @Query("DELETE FROM LearningProgress lp WHERE lp.course.id = :courseId")
    void deleteByCourseId(@Param("courseId") Integer courseId);


    @Query("SELECT lp FROM LearningProgress lp JOIN FETCH lp.chapter WHERE lp.user.id = :userId")
    List<LearningProgress> findByUser_Id(@Param("userId") Integer userId);

    // 计算用户已选课程数量
    int countByUser_Id(Integer userId);

    // 计算用户已完成章节数量
    int countByUser_IdAndCompletedTrue(Integer userId);

    // 添加计算未完成课程数量的方法
    int countByUser_IdAndCompletedFalse(Integer userId);
}