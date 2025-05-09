package staff.training.system.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import staff.training.system.security.JwtAuthFilter;


//此Config實現了基於JWT的無狀態認證機制確保API支持前後端分離架構
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    @Lazy
    private JwtAuthFilter jwtAuthFilter;
    //允許前端(http://localhost:5173)與後端API交互
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))  //啟用Cors配置
            .csrf(AbstractHttpConfigurer::disable) //禁用CSRF保護，使用JWT進行身份驗證
            .authorizeHttpRequests(auth -> auth 
                .requestMatchers(              //處理API請求
                    "/api/auth/**",
                    "/api/courses",
                    "/api/courses/**",
                    "/api/progress/**",
                    "/api/enrollment/**"
                ).permitAll()
                .requestMatchers(HttpMethod.POST, "/api/courses").hasRole("ADMIN") //處理各種HTML請求並進行權限限制
                .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll() 
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/enrollment/**").hasRole("ADMIN")
                .anyRequest().authenticated() 
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) //設置無狀態會話管理，禁用Session
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); //添加JWT過濾器，處理身份驗證請求
        return http.build();
    }
    //配置Cors，允許前端與後端API交互發起誇域請求
    @Bean
    public CorsConfigurationSource corsConfigurationSource() { 
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173",
        "staff-training-system-git-main-tinmozs-projects.vercel.app",
        "https://staff-training-system.vercel.app/")); //前端地址
        config.setAllowedMethods(List.of("*")); //允許所有HTTP方法
        config.setAllowedHeaders(List.of("*")); //允許所有HTTP標頭
        config.setAllowCredentials(true); //允許攜帶憑證
        config.setMaxAge(3600L); //預檢請求的最大有效期

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); //註冊Cors,讓所有API路徑應用Cor規則
        return source;
    }
 
    @Bean
    public AuthenticationManager authenticationManager( //使用AuthenticationManager來處理身份驗證
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean   
    public PasswordEncoder passwordEncoder() { //使用BCrypt加密處理密碼
        return new BCryptPasswordEncoder();
    }
    
    
    
}