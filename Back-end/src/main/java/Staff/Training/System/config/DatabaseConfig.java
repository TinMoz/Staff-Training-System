package staff.training.system.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfig {
    
    @Value("${spring.datasource.url:未配置}")
    private String databaseUrl;
    
    @Value("${spring.datasource.username:未配置}")
    private String databaseUsername;
    
    public String getDatabaseName() {
        // 從JDBC URL中提取數據庫名稱
        if (databaseUrl.contains("jdbc:")) {
            String[] parts = databaseUrl.split("/");
            if (parts.length > 0) {
                String lastPart = parts[parts.length - 1];
                // 移除可能的查詢參數
                if (lastPart.contains("?")) {
                    return lastPart.split("\\?")[0];
                }
                return lastPart;
            }
        }
        return "未知數據庫";
    }
    
    public String getDatabaseUrl() {
        return databaseUrl;
    }
    
    public String getDatabaseUsername() {
        return databaseUsername;
    }
}