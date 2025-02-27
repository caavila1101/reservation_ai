package com.reservationai.reservation.domain;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class RestaurantDetail {

    private Long id;
    private String name;
    private String address;
    private String description;
    private String url;
    private String city;

    public RestaurantDetail(Long id, String name, String address, String description, String url, String city) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.description = description;
        this.url = url;
        this.city = city;
    }


}
