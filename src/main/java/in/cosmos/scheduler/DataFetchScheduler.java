package in.cosmos.scheduler;

import in.cosmos.service.AsteroidDetailsService;
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
    private final AsteroidDetailsService asteroidDetailsService;

    public DataFetchScheduler(AsteroidService asteroidService,
                              EarthEventService earthEventService,
                              AsteroidDetailsService asteroidDetailsService) {
        this.asteroidService = asteroidService;
        this.earthEventService = earthEventService;
        this.asteroidDetailsService = asteroidDetailsService;
    }

    // Close approaches and Earth events: one NASA call each, every hour
    @Scheduled(cron = "${cosmos.schedule.fetch:0 0 * * * *}", zone = "UTC")
    public void fetchData() {
        try {
            asteroidService.fetchAndSaveAsteroids();
            log.info("Close approaches refreshed");
        } catch (Exception e) {
            log.error("Close approach refresh failed: {}", e.getMessage(), e);
        }
        try {
            earthEventService.fetchAndSaveEvents();
            log.info("Earth events refreshed");
        } catch (Exception e) {
            log.error("Earth event refresh failed: {}", e.getMessage(), e);
        }
    }

    // SBDB details for new or stale asteroids, every 6 hours (also shortly after startup)
    @Scheduled(initialDelayString = "${cosmos.schedule.details-initial-delay:PT2M}",
               fixedDelayString = "${cosmos.schedule.details-interval:PT6H}")
    public void refreshDetails() {
        try {
            asteroidDetailsService.refreshMissingAndStale();
        } catch (Exception e) {
            log.error("Details refresh failed: {}", e.getMessage(), e);
        }
    }

    // Once a day, drop approaches that have passed so the tables don't keep growing
    @Scheduled(cron = "${cosmos.schedule.cleanup:0 30 3 * * *}", zone = "UTC")
    public void deletePassed() {
        try {
            asteroidService.deletePassedApproaches();
        } catch (Exception e) {
            log.error("Cleanup failed: {}", e.getMessage(), e);
        }
    }
}
