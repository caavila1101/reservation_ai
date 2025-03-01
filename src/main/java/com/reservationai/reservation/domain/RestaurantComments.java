package com.reservationai.reservation.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class RestaurantComments {

    private String id;
    private String cityRestaurant;
    private String nameRestaurant;
    private String comments;
    private String rating;
    private LocalDateTime createdAt;

}
