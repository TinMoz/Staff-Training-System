package staff.training.system.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionCheck implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionCheck.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        try {
            String dbInfo = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
            logger.info("成功連接到數據庫: {}", dbInfo);
        } catch (Exception e) {
            logger.error("數據庫連接失敗: {}", e.getMessage());
            // 可以選擇在連接失敗時拋出異常終止應用啟動
            // throw new RuntimeException("無法連接到數據庫，應用無法啟動", e);
        }
    }
}