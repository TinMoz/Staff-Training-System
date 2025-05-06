package staff.training.system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import staff.training.system.model.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.createdBy WHERE c.id = :id")
    Optional<Course> findByIdWithCreator(@Param("id") Integer id);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.chapters WHERE c.id = :id")
    Optional<Course> findByIdWithChapters(@Param("id") Integer id);
}