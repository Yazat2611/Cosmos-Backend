package in.cosmos.controller;

import in.cosmos.dto.AsteroidDetailDTO;
import in.cosmos.model.AsteroidApproach;
import in.cosmos.service.AsteroidDetailsService;
import in.cosmos.service.AsteroidService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asteroids")
public class AsteroidController {

    private final AsteroidService asteroidService;
    private final AsteroidDetailsService asteroidDetailsService;

    public AsteroidController(AsteroidService asteroidService, AsteroidDetailsService asteroidDetailsService) {
        this.asteroidService = asteroidService;
        this.asteroidDetailsService = asteroidDetailsService;
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


    @GetMapping("/details/{designation}")
    public ResponseEntity<AsteroidDetailDTO> getAsteroidDetails(@PathVariable String designation) {
        AsteroidDetailDTO details = asteroidDetailsService.getAsteroidDetails(designation);
        return ResponseEntity.ok(details);
    }
}
