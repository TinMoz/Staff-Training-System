package staff.training.system.repository;

import jakarta.transaction.Transactional;
import staff.training.system.model.entity.EnrollmentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

// 報名請求倉庫接口
public interface EnrollmentRequestRepository extends JpaRepository<EnrollmentRequest, Integer> {
    // 計算用戶的報名請求數量
    int countByUser_IdAndStatus(Integer userId, EnrollmentRequest.EnrollmentStatus status);
    // 根據狀態查詢報名請求
    @Query("SELECT er FROM EnrollmentRequest er " +
           "JOIN FETCH er.course " + 
           "JOIN FETCH er.user " +    
           "WHERE er.status = :status")
    List<EnrollmentRequest> findByStatus(@Param("status") EnrollmentRequest.EnrollmentStatus status);
    // 根據用戶ID和課程ID查詢報名請求是否存在
    boolean existsByUser_IdAndCourse_Id(Integer userId, Integer courseId);
    // 根據用戶ID、課程ID和狀態查詢報名請求是否存在
    boolean existsByUser_IdAndCourse_IdAndStatusIn(
        Integer userId, 
        Integer courseId, 
        List<EnrollmentRequest.EnrollmentStatus> statuses
    );
    // 根據用戶ID和課程ID刪除報名請求
    @Modifying
    @Transactional
    @Query("DELETE FROM EnrollmentRequest er WHERE er.user.id = :userId AND er.course.id = :courseId")
    void deleteByUserAndCourse(@Param("userId") Integer userId, @Param("courseId") Integer courseId);
    // 根據用戶ID和課程ID查詢報名請求
    List<EnrollmentRequest> findByUser_IdAndCourse_Id(Integer userId, Integer courseId);

   // 根據用戶ID和狀態查詢報名請求
    List<EnrollmentRequest> findByUser_IdAndStatus(Integer userId, EnrollmentRequest.EnrollmentStatus status);
}