package in.cosmos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsteroidDetailDTO {

    private AsteroidObjectDTO object;
    private AsteroidOrbitDTO orbit;
}
