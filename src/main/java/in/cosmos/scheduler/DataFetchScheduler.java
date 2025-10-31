package in.cosmos.scheduler;

import in.cosmos.service.AsteroidService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataFetchScheduler {
    private final AsteroidService asteroidService;

    public DataFetchScheduler(AsteroidService asteroidService) {
        this.asteroidService = asteroidService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void fetchData() {
        try {
            asteroidService.fetchAndSaveAsteroids();
            log.info("✅ Scheduler completed successfully");
        } catch (Exception e) {
            log.error("Scheduler failed {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
