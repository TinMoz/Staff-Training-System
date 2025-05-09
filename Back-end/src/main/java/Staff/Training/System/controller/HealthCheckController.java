package staff.training.system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/database")
    public ResponseEntity<Map<String, Object>> checkDatabaseConnection() {
        Map<String, Object> response = new HashMap<>();

        try {
            // 嘗試執行一個簡單的 SQL 查詢
            String dbStatus = jdbcTemplate.queryForObject("SELECT 1", String.class);
            response.put("status", "UP");
            response.put("message", "數據庫連接正常");
            
            // 獲取數據庫信息
            String dbInfo = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            response.put("database", dbInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("message", "數據庫連接失敗");
            response.put("error", e.getMessage());
            return ResponseEntity.status(503).body(response);
        }
    }
}