package com.reservationai.reservation.application.usecase.redirect;

import com.reservationai.reservation.infrastructure.api.dto.RestaurantCommentsDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class CreateCommentRouter {

    private final WebClient webClient;

    public CreateCommentRouter(WebClient.Builder webClient) {
        this.webClient = webClient.baseUrl("http://localhost:9090/reservation").build();
    }

    public String createCommentsRestaurant(RestaurantCommentsDTO restaurantCommentsDTO){
        return webClient.post()
                .uri("/create-comment-restaurant")
                .bodyValue(restaurantCommentsDTO)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
