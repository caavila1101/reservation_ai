package com.reservationai.reservation.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class OwnRestaurant {

    private String id;
    private String user;
    private String email;
    private String password;
    private Boolean hasPremiumAccess;
    private LocalDateTime createdAt;

}
