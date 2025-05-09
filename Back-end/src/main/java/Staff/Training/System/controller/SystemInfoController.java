package staff.training.system.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import staff.training.system.config.DatabaseConfig;

@RestController
@RequestMapping("/api/system")
public class SystemInfoController {
    
    @Autowired
    private DatabaseConfig databaseConfig;
    
    @GetMapping("/database-info")
    public Map<String, String> getDatabaseInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("databaseName", databaseConfig.getDatabaseName());
        info.put("databaseUrl", databaseConfig.getDatabaseUrl());
        info.put("databaseUsername", databaseConfig.getDatabaseUsername());
        return info;
    }
}