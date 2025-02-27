package com.reservationai.reservation.infrastructure.persistence.entities;

import com.reservationai.reservation.domain.Restaurant;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurants")
@Getter
@Setter
public class RestaurantEntity {

    @Id
    private String id;

    private String name;
    private String category;
    private String city;
    private String userOwner;
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public Restaurant toDomain(){
        return Restaurant.builder()
                .id(this.id)
                .name(this.name)
                .category(this.category)
                .city(this.city)
                .createdAt(createdAt)
                .build();
    }
}
