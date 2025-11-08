package in.cosmos.scheduler;

import in.cosmos.service.AsteroidService;
import in.cosmos.service.EarthEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataFetchScheduler {
    private final AsteroidService asteroidService;
    private final EarthEventService earthEventService;

    public DataFetchScheduler(AsteroidService asteroidService,EarthEventService earthEventService) {
        this.asteroidService = asteroidService;
        this.earthEventService = earthEventService;
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void fetchData() {
        try {
            asteroidService.fetchAndSaveAsteroids();
            earthEventService.fetchAndSaveEvents();
            log.info("✅ Scheduler completed successfully");
        } catch (Exception e) {
            log.error("Scheduler failed {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
