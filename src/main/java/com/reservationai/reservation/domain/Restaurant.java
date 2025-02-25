package com.reservationai.reservation.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Restaurant {
    private Long id;
    private String name;
    private String category;
    private String city;

    public Restaurant(Long id, String name, String category, String city) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.city = city;
    }

}
