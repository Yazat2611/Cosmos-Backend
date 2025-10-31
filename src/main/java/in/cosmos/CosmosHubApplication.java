package in.cosmos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CosmosHubApplication {

    public static void main(String[] args) {
        SpringApplication.run(CosmosHubApplication.class, args);
    }

}
