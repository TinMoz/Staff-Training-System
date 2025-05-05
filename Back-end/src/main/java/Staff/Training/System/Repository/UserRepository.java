package Staff.Training.System.Repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Staff.Training.System.User;

public interface UserRepository extends JpaRepository<User, Integer> {  // 主键类型改为Integer
    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    boolean existsByEmail(String email);
}