package in.cosmos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OribitalElementDTO {
    private String title;
    private String value;
    private String name;
    private String sigma;
    private String units;
    private String label;
}
