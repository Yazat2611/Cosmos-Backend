package in.cosmos.repository;

import in.cosmos.model.AsteroidDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface AsteroidDetailsRepository extends JpaRepository<AsteroidDetails, String> {

    // Details for asteroids that no longer have any stored approach
    @Modifying
    @Query("DELETE FROM AsteroidDetails d WHERE NOT EXISTS (SELECT 1 FROM AsteroidApproach a WHERE a.designation = d.designation)")
    int deleteOrphans();
}
