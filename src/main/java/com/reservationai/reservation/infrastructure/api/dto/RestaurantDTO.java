package com.reservationai.reservation.infrastructure.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RestaurantDTO {
    private String name;
    private String category;
    private String city;
    private String address;
    private String description;
    private String url;

}
