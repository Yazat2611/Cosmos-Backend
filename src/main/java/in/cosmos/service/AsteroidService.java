package in.cosmos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.cosmos.client.NasaCadClient;
import in.cosmos.dto.NasaApiResponseDTO;
import in.cosmos.model.AsteroidApproach;
import in.cosmos.repository.AsteroidApproachRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Slf4j
@Service
public class AsteroidService {

    private final AsteroidApproachRepository asteroidApproachRepository;
    private final NasaCadClient nasaCadClient;
    private final ObjectMapper objectMapper;

    public AsteroidService(AsteroidApproachRepository asteroidApproachRepository
    , NasaCadClient nasaCadClient
    , ObjectMapper objectMapper) {
        this.asteroidApproachRepository = asteroidApproachRepository;
        this.nasaCadClient = nasaCadClient;
        this.objectMapper = objectMapper;
    }

    public void fetchAndSaveAsteroids() {
        String jsonString = nasaCadClient.fetchCloseApproachData();

        log.info("Got JSON response from NASA successfully");

        try {
            NasaApiResponseDTO apiResponse = objectMapper.readValue(jsonString, NasaApiResponseDTO.class);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MMM-dd HH:mm", Locale.ENGLISH);

            log.info("Parsed Json Successfully");
            log.info("Total asteroids: {}", apiResponse.getCount());

            List<AsteroidApproach> asteroidApproaches = new ArrayList<>();

            if (apiResponse.getData() == null) {
                log.info("No close approaches in range");
                return;
            }

            // Look columns up by name so a change in CAD's field order doesn't shift values
            Map<String, Integer> col = new HashMap<>();
            for (int i = 0; i < apiResponse.getFields().size(); i++) {
                col.put(apiResponse.getFields().get(i), i);
            }

            for(List<String> row:apiResponse.getData()) {
                String designation = row.get(col.get("des"));
                LocalDateTime closeApproachDate = LocalDateTime.parse(row.get(col.get("cd")), formatter);

                // Update the existing row for this approach instead of inserting a duplicate
                AsteroidApproach asteroidApproach = asteroidApproachRepository
                        .findFirstByDesignationAndCloseApproachDate(designation, closeApproachDate)
                        .orElse(new AsteroidApproach());

                asteroidApproach.setDesignation(designation);
                asteroidApproach.setOrbitId(row.get(col.get("orbit_id")));
                asteroidApproach.setJulianDate(Double.parseDouble(row.get(col.get("jd"))));
                asteroidApproach.setCloseApproachDate(closeApproachDate);
                asteroidApproach.setDistanceAu(toDecimal(row, col, "dist"));
                asteroidApproach.setMinimumDistance(toDecimal(row, col, "dist_min"));
                asteroidApproach.setMaximumDistance(toDecimal(row, col, "dist_max"));
                asteroidApproach.setRelativeVelocity(toDecimal(row, col, "v_rel"));
                asteroidApproach.setInfVelocity(toDecimal(row, col, "v_inf"));
                asteroidApproach.setTimeUncertanity(row.get(col.get("t_sigma_f")));
                asteroidApproach.setAbsoluteMagnitude(toDecimal(row, col, "h"));

                asteroidApproaches.add(asteroidApproach);
            }

            asteroidApproachRepository.saveAll(asteroidApproaches);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BigDecimal toDecimal(List<String> row, Map<String, Integer> col, String field) {
        Integer index = col.get(field);
        String value = index == null ? null : row.get(index);
        return value == null ? null : new BigDecimal(value);
    }

    public List<AsteroidApproach> getUpcomingApproaches() {
        // CAD dates are UTC
        return asteroidApproachRepository.findByCloseApproachDateGreaterThanEqualOrderByCloseApproachDateAsc(
                LocalDateTime.now(ZoneOffset.UTC));
    }
}
