package staff.training.system.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import staff.training.system.model.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {  // 主键类型改为Integer
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
}