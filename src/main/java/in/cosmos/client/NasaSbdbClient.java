package in.cosmos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NasaSbdbClient {

    @Value("${nasa.api.base-url.sbdb}")
    private String baseUrl;

    private final WebClient webClient;

    public NasaSbdbClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public String fetchAsteroidDetails(String designation) {
        String url = baseUrl + "?sstr=" + designation;

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
