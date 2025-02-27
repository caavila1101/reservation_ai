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
@Table(name = "restaurant_owner")
@Getter
@Setter
public class RestaurantOwnerEntity {

    @Id
    private String id;
    private String user;
    private String email;
    private String password;
    @Column(name = "has_premium_access")
    private Boolean hasPremiumAccess;
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;



}
