package in.cosmos.controller;

import in.cosmos.model.EarthEvents;
import in.cosmos.service.EarthEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/earth-events")
@Slf4j
public class EarthController {
    private final EarthEventService earthEventService;

    public  EarthController(EarthEventService earthEventService) {
        this.earthEventService =   earthEventService;
    }

    @PostMapping("/fetch")
    public ResponseEntity<String> fetchEvents() throws JsonProcessingException {
        earthEventService.fetchAndSaveEvents();
        return ResponseEntity.ok("Earth events fetched and saved successfully");
    }

    @GetMapping
    public ResponseEntity<List<EarthEvents>> getAllEvents() {
        List<EarthEvents> events = earthEventService.getAllEvents();
        return ResponseEntity.ok(events);
    }
}
