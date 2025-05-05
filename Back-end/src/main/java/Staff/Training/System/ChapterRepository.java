package Staff.Training.System;


import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ChapterRepository extends JpaRepository<chapter, Integer> {
    Optional<chapter> findFirstByCourseIdOrderByOrderNumAsc(Integer courseId);
}