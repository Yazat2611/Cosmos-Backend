package in.cosmos.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class EarthEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="event_id")
    private String eventId;

    private String title;

    private String description;

    private String link;

    private LocalDateTime closed;

    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "category_title")
    private String categoryTitle;

    @Column(name = "source_id")
    private String sourceId;

    @Column(name = "source_url")
    private String sourceUrl;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "earthEvents", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EarthEventGeometry> geometries = new ArrayList<>();
}
