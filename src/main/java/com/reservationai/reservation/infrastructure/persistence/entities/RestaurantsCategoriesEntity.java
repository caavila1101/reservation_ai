package com.reservationai.reservation.infrastructure.persistence.entities;

import com.reservationai.reservation.domain.RestaurantCategory;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurant_categories")
@Getter
@Setter
public class RestaurantsCategoriesEntity {
    @Id
    private String id;
    private String name;

    public RestaurantCategory toDomain(){
        return RestaurantCategory.builder()
                .name(name)
                .build();
    }
}
