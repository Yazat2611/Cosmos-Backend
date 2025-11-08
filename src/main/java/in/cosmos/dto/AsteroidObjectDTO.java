package in.cosmos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsteroidObjectDTO {
    private String spkid;
    @JsonProperty("orbit_id")
    private String orbitId;
    private Boolean pha;
    private Boolean neo;
    private String des;
    @JsonProperty("orbit_class")
    private OrbitalClassDTO orbitClass;
    @JsonProperty("full_name")
    private String fullName;
    private String kind;
    private String prefix;
}
