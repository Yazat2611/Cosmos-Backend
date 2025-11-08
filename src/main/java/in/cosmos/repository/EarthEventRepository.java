package in.cosmos.repository;

import in.cosmos.model.EarthEvents;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EarthEventRepository extends JpaRepository<EarthEvents, Long> {
    Optional<EarthEvents> findByEventId(String eventId);
}
