package com.reservationai.reservation.application.usecase.redirect;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class RestaurantByNameRouter {

    private final WebClient webClient;

    public RestaurantByNameRouter(WebClient.Builder webClient) {
        this.webClient = webClient.baseUrl("http://localhost:9090/reservation").build();
    }

    public String searchRestaurantsByName(String name) {
        return webClient.get()
                .uri(
                        "/get-restaurant-by-name?name={name}",
                        Map.of(
                                "name", name
                        )
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

}
