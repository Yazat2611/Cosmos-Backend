package in.cosmos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data
public class EarthEventGeometry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "earth_events_id",nullable = false)
    private EarthEvents earthEvents;

    @Column(name = "magnitude_value")
    private Double magnitudeValue;

    @Column(name = "magnitude_unit")
    private String magnitudeUnit;

    @Column(name = "observation_date")
    private LocalDateTime observationDate;

    @Column(name = "geometry_type")
    private String geometryType;

    private Double longitude;
    private Double latitude;
}
