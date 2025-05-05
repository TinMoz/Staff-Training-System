package Staff.Training.System.Security;

import Staff.Training.System.User;
import Staff.Training.System.Repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service // 关键注解，注册为 Bean
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    // 构造函数注入 UserRepository
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在"));

        // 确保角色格式为 ROLE_ADMIN 或 ROLE_USER
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().name()) // 确保角色前缀为 ROLE_
                .build();
    }
}