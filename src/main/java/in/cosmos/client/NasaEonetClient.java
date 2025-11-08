package in.cosmos.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
public class NasaEonetClient {

    private final WebClient webClient;

    @Value("${nasa.api.base-url.eonet}")
    private String baseUrl;

    public NasaEonetClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public String fetchEarthEvents() {
        log.info("Fetching earth events");
        try {
            String response = webClient.get()
                    .uri(baseUrl + "/events?status=open&limit=10")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("Earth events response: {}", response);
            return response;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
}
