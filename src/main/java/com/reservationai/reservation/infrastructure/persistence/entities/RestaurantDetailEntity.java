package com.reservationai.reservation.infrastructure.persistence.entities;

import com.reservationai.reservation.domain.RestaurantDetail;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurant_info")
@Getter
@Setter
public class RestaurantDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;
    private String description;
    private String url;
    private String city;

    public RestaurantDetail toDomain(){
        return RestaurantDetail.builder()
                .id(this.id)
                .name(this.name)
                .address(this.address)
                .description(this.description)
                .url(this.url)
                .city(this.city)
                .build();
    }
}
