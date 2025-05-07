package staff.training.system.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import staff.training.system.model.entity.User;
import staff.training.system.repository.UserRepository;

//此為提供用戶認證數據
@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;    // 用戶倉庫接口

    // 加載用戶信息
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username) // 根據用戶名查詢用戶 如果沒有則報錯
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

        // 輸出用戶詳細信息
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name()) // 确保角色前缀为 ROLE_
                .build();
    }
}