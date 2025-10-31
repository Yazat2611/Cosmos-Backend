package in.cosmos.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name="asteroid_approach")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class AsteroidApproach {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String designation;

    @Column(name = "orbit_id")
    private String orbitId;

    @Column(name = "julian_date")
    private Double julianDate;

    @Column(name="close_approach_date")
    private LocalDateTime closeApproachDate;

    @Column(name="min_distance")
    private BigDecimal minimumDistance;

    @Column(name="max_distance")
    private BigDecimal maximumDistance;

    @Column(name = "relative_velocity")
    private BigDecimal relativeVelocity;

    @Column(name = "inf_velocity")
    private BigDecimal infVelocity;

    @Column(name = "time_uncertanity")
    private String timeUncertanity;

    @Column(name = "absolute_magnitude")
    private BigDecimal absoluteMagnitude;

    @Column(name="distance_au")
    private BigDecimal distanceAu;
}
