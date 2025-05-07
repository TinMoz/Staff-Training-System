package staff.training.system.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import staff.training.system.model.entity.Course;

// 課程倉庫接口
public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.createdBy WHERE c.id = :id") 
    Optional<Course> findByIdWithCreator(@Param("id") Integer id); // 根據ID查找課程並獲取創建者信息

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.chapters WHERE c.id = :id")
    Optional<Course> findByIdWithChapters(@Param("id") Integer id); // 根據ID查找課程並獲取章節信息
}