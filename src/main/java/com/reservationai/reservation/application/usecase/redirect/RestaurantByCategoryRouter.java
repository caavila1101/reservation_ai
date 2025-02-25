package com.reservationai.reservation.application.usecase.redirect;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class RestaurantByCategoryRouter {
    private final WebClient webClient;

    public RestaurantByCategoryRouter(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:9090/reservation").build();
    }

    public String searchRestaurants(String category, String city) {
        return webClient.get()
                .uri(
                        "/get-restaurants-by-category?category={category}&city={city}",
                        Map.of(
                                "category", category,
                                "city", city
                        )
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
