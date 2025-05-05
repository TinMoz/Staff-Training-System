package Staff.Training.System.Repository;

import Staff.Training.System.Course;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Integer> {
    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.createdBy WHERE c.id = :id")
    Optional<Course> findByIdWithCreator(@Param("id") Integer id);

    @Query("SELECT c FROM Course c LEFT JOIN FETCH c.chapters WHERE c.id = :id")
    Optional<Course> findByIdWithChapters(@Param("id") Integer id);
}