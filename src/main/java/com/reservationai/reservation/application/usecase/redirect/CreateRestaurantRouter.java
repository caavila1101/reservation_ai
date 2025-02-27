package com.reservationai.reservation.application.usecase.redirect;

import com.reservationai.reservation.infrastructure.api.dto.RestaurantDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CreateRestaurantRouter {

    private final WebClient webClient;

    public CreateRestaurantRouter(WebClient.Builder webClient) {
        this.webClient = webClient.baseUrl("http://localhost:9090/reservation").build();
    }

    public String createRestaurant(RestaurantDTO data){
        return webClient.post()
                .uri("/create-resturant")
                .bodyValue(data)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
