package com.reservationai.reservation.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reviews")
@Getter
@Setter
public class RestaurantReviewsEntity {

    @Id
    private String id;
    @Column(name = "restaurant_name")
    private String nameRestaurant;
    @Column(name = "restaurant_city")
    private String restaurantCity;
    private String comment;
    private Integer rating;
    @Column(updatable = false)
    private LocalDateTime createdAt;

}
