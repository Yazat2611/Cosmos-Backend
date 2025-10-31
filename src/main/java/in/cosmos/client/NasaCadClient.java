package in.cosmos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class NasaCadClient {
    private final WebClient webClient;

    @Value("${nasa.api.base-url.cad}")
    private String cadUrl;

    public NasaCadClient(WebClient webClient){
        this.webClient = webClient;
    }

    public String fetchCloseApproachData() {
        String url = cadUrl + "?date-min=2025-10-30&date-max=2025-12-31&dist-max=0.5";
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
