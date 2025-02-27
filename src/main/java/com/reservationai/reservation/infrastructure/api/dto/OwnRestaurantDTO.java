package com.reservationai.reservation.infrastructure.api.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OwnRestaurantDTO {
    private String user;
    private String email;
    private String password;
}
