package staff.training.system.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import staff.training.system.model.entity.Chapter;
import java.util.Optional;

// 章節倉庫接口
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    Optional<Chapter> findFirstByCourseIdOrderByOrderNumAsc(Integer courseId); // 根据课程ID查找第一个章节
}