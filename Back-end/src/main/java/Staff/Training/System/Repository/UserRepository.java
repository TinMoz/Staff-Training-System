package staff.training.system.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import staff.training.system.model.entity.User;
// 此為用戶倉庫接口
public interface UserRepository extends JpaRepository<User, Integer> { 
    // 根據用戶名查詢用戶是否已存在
    boolean existsByUsername(String username);
    // 根據電子郵件查詢用戶是否已存在
    Optional<User> findByUsername(String username);
    // 根據Email查詢用戶是否已存在
    boolean existsByEmail(String email);
}