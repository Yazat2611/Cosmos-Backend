package in.cosmos.controller;

import in.cosmos.model.AsteroidApproach;
import in.cosmos.service.AsteroidService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/asteroids")
public class AsteroidController {

    private final AsteroidService asteroidService;

    public AsteroidController(AsteroidService asteroidService) {
        this.asteroidService = asteroidService;
    }

    @PostMapping("/fetch")
    public ResponseEntity<String> fetchAsteroids() {
        asteroidService.fetchAndSaveAsteroids();
        return ResponseEntity.ok("Asteroids fetched and saved successfully");
    }

    @GetMapping("/all")
    public ResponseEntity<List<AsteroidApproach>>  getAllAsteroids() {
        List<AsteroidApproach> approaches = asteroidService.getUpcomingApproaches();

        return  ResponseEntity.ok(approaches);
    }
}
