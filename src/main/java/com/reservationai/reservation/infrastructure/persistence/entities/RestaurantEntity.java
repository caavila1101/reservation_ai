package com.reservationai.reservation.infrastructure.persistence.entities;

import com.reservationai.reservation.domain.Restaurant;
import jakarta.persistence.*;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurants")
@Getter
@Setter
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String category;
    private String city;

    public Restaurant toDomain(){
        return Restaurant.builder()
                .id(this.id)
                .name(this.name)
                .category(this.category)
                .city(this.city)
                .build();
    }
}
