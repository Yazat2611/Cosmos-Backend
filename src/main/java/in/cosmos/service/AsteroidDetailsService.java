package in.cosmos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import in.cosmos.client.NasaSbdbClient;
import in.cosmos.dto.AsteroidDetailDTO;
import in.cosmos.dto.AsteroidObjectDTO;
import in.cosmos.model.AsteroidDetails;
import in.cosmos.repository.AsteroidApproachRepository;
import in.cosmos.repository.AsteroidDetailsRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AsteroidDetailsService {
    private final NasaSbdbClient nasaSbdbClient;
    private final ObjectMapper objectMapper;
    private final AsteroidDetailsRepository detailsRepository;
    private final AsteroidApproachRepository approachRepository;

    // Orbital elements barely change, so a stored lookup is refreshed only after this long
    @Value("${cosmos.details.max-age:P7D}")
    private Duration maxAge;

    // Pause between SBDB calls during a refresh, so NASA only ever sees one request at a time
    @Value("${cosmos.details.request-gap:PT1S}")
    private Duration requestGap;

    public AsteroidDetailsService(NasaSbdbClient nasaSbdbClient,
                                  ObjectMapper objectMapper,
                                  AsteroidDetailsRepository detailsRepository,
                                  AsteroidApproachRepository approachRepository) {
        this.nasaSbdbClient = nasaSbdbClient;
        this.objectMapper = objectMapper;
        this.detailsRepository = detailsRepository;
        this.approachRepository = approachRepository;
    }

    // Reads from the database; NASA is only called when this asteroid has never been stored
    public AsteroidDetailDTO getAsteroidDetails(String designation) {
        AsteroidDetails stored = detailsRepository.findById(designation)
                .orElseGet(() -> fetchAndSave(designation));
        return parse(stored);
    }

    public Map<String, AsteroidDetails> findAll(Set<String> designations) {
        return detailsRepository.findAllById(designations).stream()
                .collect(Collectors.toMap(AsteroidDetails::getDesignation, Function.identity()));
    }

    // Looks up upcoming asteroids that are missing or stale, one SBDB call at a time
    public void refreshMissingAndStale() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        LocalDateTime staleBefore = now.minus(maxAge);

        List<String> upcoming = approachRepository.findUpcomingDesignations(now);
        Map<String, AsteroidDetails> stored = findAll(Set.copyOf(upcoming));

        List<String> due = new ArrayList<>();
        for (String designation : upcoming) {
            AsteroidDetails d = stored.get(designation);
            if (d == null || d.getFetchedAt().isBefore(staleBefore)) {
                due.add(designation);
            }
        }
        log.info("Details refresh: {} upcoming asteroids, {} missing or stale", upcoming.size(), due.size());

        int saved = 0;
        for (String designation : due) {
            try {
                fetchAndSave(designation);
                saved++;
            } catch (Exception e) {
                log.warn("Details refresh failed for {}: {}", designation, e.getMessage());
            }
            if (!pause()) {
                break;
            }
        }
        log.info("Details refresh: saved {} of {}", saved, due.size());
    }

    @Transactional
    public int deleteOrphans() {
        return detailsRepository.deleteOrphans();
    }

    private AsteroidDetails fetchAndSave(String designation) {
        String json = nasaSbdbClient.fetchAsteroidDetails(designation);
        AsteroidDetailDTO dto = readDto(json);
        if (dto.getObject() == null || dto.getOrbit() == null) {
            // SBDB answers 200 with a message (or a candidate list) when the lookup isn't a single object
            throw new IllegalStateException("SBDB has no single record for " + designation);
        }

        AsteroidDetails details = detailsRepository.findById(designation).orElseGet(AsteroidDetails::new);
        AsteroidObjectDTO object = dto.getObject();
        details.setDesignation(designation);
        details.setFullName(object.getFullName() == null ? null : object.getFullName().trim());
        details.setPha(object.getPha());
        details.setNeo(object.getNeo());
        if (object.getOrbitClass() != null) {
            details.setOrbitClassCode(object.getOrbitClass().getCode());
            details.setOrbitClassName(object.getOrbitClass().getName());
        }
        details.setPayload(json);
        details.setFetchedAt(LocalDateTime.now(ZoneOffset.UTC));
        return detailsRepository.save(details);
    }

    private AsteroidDetailDTO parse(AsteroidDetails stored) {
        return readDto(stored.getPayload());
    }

    private AsteroidDetailDTO readDto(String json) {
        try {
            return objectMapper.readValue(json, AsteroidDetailDTO.class);
        } catch (Exception e) {
            throw new IllegalStateException("Could not parse SBDB response", e);
        }
    }

    private boolean pause() {
        try {
            Thread.sleep(requestGap.toMillis());
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
}
