package staff.training.system.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import staff.training.system.model.entity.Chapter;

import java.util.Optional;

public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    Optional<Chapter> findFirstByCourseIdOrderByOrderNumAsc(Integer courseId);
}