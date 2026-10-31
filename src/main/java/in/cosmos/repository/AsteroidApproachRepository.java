package in.cosmos.repository;

import in.cosmos.model.AsteroidApproach;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AsteroidApproachRepository extends JpaRepository<AsteroidApproach,Long> {
    Optional<AsteroidApproach> findFirstByDesignationAndCloseApproachDate(String designation, LocalDateTime closeApproachDate);

    List<AsteroidApproach> findByCloseApproachDateGreaterThanEqualOrderByCloseApproachDateAsc(LocalDateTime from);
}
