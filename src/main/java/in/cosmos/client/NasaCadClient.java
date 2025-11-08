package in.cosmos.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;

@Component
public class NasaCadClient {
    private final WebClient webClient;

    @Value("${nasa.api.base-url.cad}")
    private String cadUrl;

    public NasaCadClient(WebClient webClient){
        this.webClient = webClient;
    }

    public String fetchCloseApproachData() {
        LocalDate today = LocalDate.now();
        LocalDate twoMonthsLater = today.plusMonths(2);

        String dateMin = today.toString();
        String dateMax = twoMonthsLater.toString();


        String url = cadUrl +
                "?date-min=" + dateMin +
                "&date-max=" + dateMax +
                "&dist-max=0.5";

        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
