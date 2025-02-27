package com.reservationai.reservation.infrastructure.persistence.entities;

import com.reservationai.reservation.domain.OwnRestaurant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

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
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public OwnRestaurant toDomain(){
        return OwnRestaurant.builder()
                .user(this.user)
                .email(this.email)
                .password(this.password)
                .build();
    }

}
