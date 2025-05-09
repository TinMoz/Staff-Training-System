package staff.training.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

// 主要應用程序，啟動Spring Boot應用程序
@SpringBootApplication
public class StaffTrainingSystemApplication {

	public static void main(String[] args) {
		 Dotenv dotenv = Dotenv.configure()
            .directory("src/main/resources")
            .ignoreIfMissing()
            .load();
        dotenv.entries().forEach(entry -> {
            if (System.getenv(entry.getKey()) == null) {
                System.setProperty(entry.getKey(), entry.getValue());
            }
        });
        SpringApplication.run(StaffTrainingSystemApplication.class, args);
    }
}


