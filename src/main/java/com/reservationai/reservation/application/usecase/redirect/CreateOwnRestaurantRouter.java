package com.reservationai.reservation.application.usecase.redirect;

import com.reservationai.reservation.infrastructure.api.dto.OwnRestaurantDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CreateOwnRestaurantRouter {
    private WebClient webClient;

    public CreateOwnRestaurantRouter(WebClient.Builder webClient) {
        this.webClient = webClient.baseUrl("http://localhost:9090/reservation").build();
    }

    public String createOwnRestaurant(OwnRestaurantDTO ownRestaurantDTO){
        return webClient.post()
                .uri("/create-own-restaurant")
                .bodyValue(ownRestaurantDTO)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
