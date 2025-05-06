package staff.training.system.repository;

import jakarta.transaction.Transactional;
import staff.training.system.model.entity.EnrollmentRequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EnrollmentRequestRepository extends JpaRepository<EnrollmentRequest, Integer> {
    // 添加新的计数方法
    int countByUser_IdAndStatus(Integer userId, EnrollmentRequest.EnrollmentStatus status);

    @Query("SELECT er FROM EnrollmentRequest er " +
           "JOIN FETCH er.course " + 
           "JOIN FETCH er.user " +    
           "WHERE er.status = :status")
    List<EnrollmentRequest> findByStatus(@Param("status") EnrollmentRequest.EnrollmentStatus status);
    
    boolean existsByUser_IdAndCourse_Id(Integer userId, Integer courseId);
    
    boolean existsByUser_IdAndCourse_IdAndStatusIn(
        Integer userId, 
        Integer courseId, 
        List<EnrollmentRequest.EnrollmentStatus> statuses
    );

    @Modifying
    @Transactional
    @Query("DELETE FROM EnrollmentRequest er WHERE er.user.id = :userId AND er.course.id = :courseId")
    void deleteByUserAndCourse(@Param("userId") Integer userId, @Param("courseId") Integer courseId);
    
    List<EnrollmentRequest> findByUser_IdAndCourse_Id(Integer userId, Integer courseId);

    /**
     * 根据用户ID和状态获取报名请求
     * @param userId 用户ID
     * @param status 报名请求状态
     * @return 符合条件的报名请求列表
     */
    List<EnrollmentRequest> findByUser_IdAndStatus(Integer userId, EnrollmentRequest.EnrollmentStatus status);
}