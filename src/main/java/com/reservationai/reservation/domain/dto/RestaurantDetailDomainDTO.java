package com.reservationai.reservation.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RestaurantDetailDomainDTO {
    private String name;
    private String category;
    private String city;
    private String address;
    private String description;
    private String url;
}
