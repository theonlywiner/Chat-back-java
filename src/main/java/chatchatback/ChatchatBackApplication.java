package chatchatback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ChatchatBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatchatBackApplication.class, args);
    }

}
