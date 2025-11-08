package in.cosmos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NasaApiResponseDTO {

    private Long count;
    List<String> fields;
    List<List<String>> data;
}
