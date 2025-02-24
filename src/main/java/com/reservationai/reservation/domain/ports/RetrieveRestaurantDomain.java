package com.reservationai.reservation.domain.ports;

import com.reservationai.reservation.domain.Restaurant;

import java.util.List;

public interface RetrieveRestaurantDomain {
    List<Restaurant> findByType(String typeRestaurant);
}
