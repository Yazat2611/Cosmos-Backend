package in.cosmos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.cosmos.client.NasaSbdbClient;
import in.cosmos.dto.AsteroidDetailDTO;
import org.springframework.stereotype.Service;

@Service
public class AsteroidDetailsService {
    private final NasaSbdbClient nasaSbdbClient;
    private final ObjectMapper objectMapper;

    public AsteroidDetailsService(NasaSbdbClient nasaSbdbClient, ObjectMapper objectMapper) {
        this.nasaSbdbClient = nasaSbdbClient;
        this.objectMapper = objectMapper;
    }

    public AsteroidDetailDTO getAsteroidDetails(String asteroidId) {
        try {
            String jsonResponse = nasaSbdbClient.fetchAsteroidDetails(asteroidId);
            return objectMapper.readValue(jsonResponse, AsteroidDetailDTO.class);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
