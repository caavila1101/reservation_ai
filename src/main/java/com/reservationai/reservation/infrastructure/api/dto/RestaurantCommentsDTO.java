package com.reservationai.reservation.infrastructure.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RestaurantCommentsDTO {

    private String cityRestaurant;
    private String nameRestaurant;
    private String comments;
    private String rating;

}
