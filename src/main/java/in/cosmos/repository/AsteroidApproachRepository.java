package in.cosmos.repository;

import in.cosmos.model.AsteroidApproach;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AsteroidApproachRepository extends JpaRepository<AsteroidApproach,Long> {
    Optional<AsteroidApproach> findFirstByDesignationAndCloseApproachDate(String designation, LocalDateTime closeApproachDate);

    List<AsteroidApproach> findByCloseApproachDateGreaterThanEqualOrderByCloseApproachDateAsc(LocalDateTime from);

    @Query("SELECT DISTINCT a.designation FROM AsteroidApproach a WHERE a.closeApproachDate >= :from")
    List<String> findUpcomingDesignations(@Param("from") LocalDateTime from);

    @Modifying
    @Query("DELETE FROM AsteroidApproach a WHERE a.closeApproachDate < :before")
    int deletePassedBefore(@Param("before") LocalDateTime before);
}
