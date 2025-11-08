package in.cosmos.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.cosmos.client.NasaEonetClient;
import in.cosmos.model.EarthEventGeometry;
import in.cosmos.model.EarthEvents;
import in.cosmos.repository.EarthEventGeometryRepository;
import in.cosmos.repository.EarthEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class EarthEventService {
    private final NasaEonetClient eonetClient;
    private final EarthEventRepository earthEventRepository;
    private final ObjectMapper objectMapper;

    public EarthEventService(NasaEonetClient eonetClient,
                             EarthEventRepository earthEventRepository,
                             ObjectMapper objectMapper) {
        this.eonetClient = eonetClient;
        this.earthEventRepository = earthEventRepository;
        this.objectMapper = objectMapper;
    }

    public void fetchAndSaveEvents() throws JsonProcessingException {
        log.info("Fetching Earth Events");

        try {
            String jsonResponse = eonetClient.fetchEarthEvents();

            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode eventsArray = rootNode.get("events");

            if(eventsArray == null || !eventsArray.isArray()) {
                log.warn("No events found");
                return;
            }

            int savedCount = 0;

            for(JsonNode event : eventsArray) {
                try{
                    saveOrUpdateEvent(event);
                    savedCount++;
                }catch (Exception e) {
                    log.error("Error processing event: {}",
                            event.has("id") ? event.get("id").asText() : "unknown", e);
                }
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }


    private void saveOrUpdateEvent(JsonNode eventNode) {
        // Get event ID
        if (!eventNode.has("id")) {
            log.warn("Event missing ID, skipping");
            return;
        }

        String eventId = eventNode.get("id").asText();

        // Find or create event
        EarthEvents event = earthEventRepository.findByEventId(eventId)
                .orElse(new EarthEvents());

        // Basic fields
        event.setEventId(eventId);
        event.setTitle(eventNode.has("title") ? eventNode.get("title").asText() : "Unknown Event");

        JsonNode descNode = eventNode.get("description");
        event.setDescription(descNode != null && !descNode.isNull() ? descNode.asText() : null);

        event.setLink(eventNode.has("link") ? eventNode.get("link").asText() : null);

        // Parse closed date
        JsonNode closedNode = eventNode.get("closed");
        if (closedNode != null && !closedNode.isNull()) {
            try {
                event.setClosed(LocalDateTime.parse(closedNode.asText(), DateTimeFormatter.ISO_DATE_TIME));
            } catch (Exception e) {
                log.warn("Failed to parse closed date for event {}", eventId);
            }
        }

        // Parse category (safely)
        JsonNode categories = eventNode.get("categories");
        if (categories != null && categories.isArray() && categories.size() > 0) {
            JsonNode category = categories.get(0);
            event.setCategoryId(category.get("id").asText());
            event.setCategoryTitle(category.get("title").asText());
        }

        // Parse source (safely)
        JsonNode sources = eventNode.get("sources");
        if (sources != null && sources.isArray() && sources.size() > 0) {
            JsonNode source = sources.get(0);
            event.setSourceId(source.has("id") ? source.get("id").asText() : null);
            event.setSourceUrl(source.has("url") ? source.get("url").asText() : null);
        }

        // Clear existing geometries
        event.getGeometries().clear();

        // Parse geometries
        JsonNode geometries = eventNode.get("geometry");
        if (geometries != null && geometries.isArray()) {
            for (JsonNode geoNode : geometries) {
                try {
                    EarthEventGeometry geometry = new EarthEventGeometry();
                    geometry.setEarthEvents(event);

                    // Magnitude (optional)
                    if (geoNode.has("magnitudeValue") && !geoNode.get("magnitudeValue").isNull()) {
                        geometry.setMagnitudeValue(geoNode.get("magnitudeValue").asDouble());
                    }
                    if (geoNode.has("magnitudeUnit") && !geoNode.get("magnitudeUnit").isNull()) {
                        geometry.setMagnitudeUnit(geoNode.get("magnitudeUnit").asText());
                    }

                    // Date (required)
                    if (geoNode.has("date")) {
                        geometry.setObservationDate(LocalDateTime.parse(
                                geoNode.get("date").asText(), DateTimeFormatter.ISO_DATE_TIME));
                    }

                    // Type
                    geometry.setGeometryType(geoNode.has("type") ? geoNode.get("type").asText() : "Point");

                    // Coordinates (required)
                    JsonNode coords = geoNode.get("coordinates");
                    if (coords != null && coords.isArray() && coords.size() >= 2) {
                        geometry.setLongitude(coords.get(0).asDouble());
                        geometry.setLatitude(coords.get(1).asDouble());

                        event.getGeometries().add(geometry);
                    } else {
                        log.warn("Invalid coordinates for event {}", eventId);
                    }

                } catch (Exception e) {
                    log.warn("Failed to parse geometry for event {}: {}", eventId, e.getMessage());
                }
            }
        }

        earthEventRepository.save(event);
        log.debug("Saved/Updated event: {} with {} geometry points", eventId, event.getGeometries().size());
    }

    public List<EarthEvents> getAllEvents() {
        return earthEventRepository.findAll();
    }

}
