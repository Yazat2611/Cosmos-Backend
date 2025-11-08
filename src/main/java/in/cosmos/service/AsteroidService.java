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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

            for(List<String> row:apiResponse.getData()) {
                AsteroidApproach asteroidApproach = new AsteroidApproach();

                asteroidApproach.setDesignation(row.get(0));
                asteroidApproach.setOrbitId(row.get(1));
                asteroidApproach.setJulianDate(Double.parseDouble(row.get(2)));
                asteroidApproach.setCloseApproachDate(LocalDateTime.parse(row.get(3), formatter));
                asteroidApproach.setDistanceAu(new BigDecimal(row.get(4)));
                asteroidApproach.setMinimumDistance(new BigDecimal(row.get(5)));
                asteroidApproach.setMaximumDistance(new BigDecimal(row.get(6)));
                asteroidApproach.setRelativeVelocity(new BigDecimal(row.get(7)));
                asteroidApproach.setInfVelocity(new BigDecimal(row.get(8)));
                asteroidApproach.setTimeUncertanity(row.get(9));
                asteroidApproach.setAbsoluteMagnitude(new BigDecimal(row.get(10)));

                asteroidApproaches.add(asteroidApproach);
            }

            asteroidApproachRepository.saveAll(asteroidApproaches);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<AsteroidApproach> getUpcomingApproaches() {
        return asteroidApproachRepository.findAll();
    }
}
