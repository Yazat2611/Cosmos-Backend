package in.cosmos.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// One SBDB lookup per asteroid; payload holds NASA's raw JSON response
@Entity
@Table(name = "asteroid_details")
@Data
@NoArgsConstructor
public class AsteroidDetails {
    @Id
    private String designation;

    @Column(name = "full_name")
    private String fullName;

    private Boolean pha;

    private Boolean neo;

    @Column(name = "orbit_class_code")
    private String orbitClassCode;

    @Column(name = "orbit_class_name")
    private String orbitClassName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;

    @Column(name = "fetched_at", nullable = false)
    private LocalDateTime fetchedAt;
}
