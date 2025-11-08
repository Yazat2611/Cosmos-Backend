package in.cosmos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsteroidOrbitDTO {
    private String rms;
    private String epoch;
    private String moid;
    @JsonProperty("n_obs_used")
    private Long nObsUsed;
    @JsonProperty("first_obs")
    private String firstObs;
    @JsonProperty("last_obs")
    private String lastObs;
    List<OribitalElementDTO> elements;
}
