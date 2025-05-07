package staff.training.system.security;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// 此為JWT認證過濾器，負責處理JWT令牌的驗證和用戶身份的設置
@Component
public class JwtAuthFilter implements Filter {
    @Autowired
    private JwtUtils jwtUtils; // 注入JWT工具類，用於生成和驗證Token
    @Autowired
    private UserDetailsService userDetailsService; // 注入用戶詳細信息服務接口，用於加載用戶信息

    //JWT認證過濾器的主要邏輯
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        // 獲取HttpServletRequest對象，並獲取請求方法和路徑
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        // 獲取請求方法
        String method = httpRequest.getMethod();
        // 獲取請求路徑
        String path = httpRequest.getRequestURI(); 
        // 如果方法是Options,或者请求路径为/api/auth/login，則直接放行 不需要獲得JWT令牌
        if ("OPTIONS".equalsIgnoreCase(method) || path.contains("/api/auth/login")) {
            chain.doFilter(request, response);
            return;
        }

        String token = extractToken(httpRequest); // 獲取JWT令牌
        // 如果令牌不為空且驗證通過，則設置用戶身份
        if (token != null && jwtUtils.validateToken(token)) {
            String username = jwtUtils.getUsernameFromToken(token); // 利用JwtUtils在Token中獲取用戶名
            String role = jwtUtils.getRoleFromToken(token); // 利用JwtUtils在Token中獲取用戶權限
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); // 根據用戶名加載用戶詳細信息

            List<GrantedAuthority> authorities = new ArrayList<>(); // 創建權限列表
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role)); // 將用戶權限添加到權限列表中
            // 創建認證對象，並設置用戶詳細信息和權限
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request, response);
    }
    // 獲取請求中的JWT令牌
    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization"); // 獲取請求頭中的Authorization字段
         // 如果請求頭不為空且以"Bearer "開頭，則獲取令牌
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        return null;
    }
}