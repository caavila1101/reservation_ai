package com.reservationai.reservation.domain.ports;

import com.reservationai.reservation.domain.Restaurant;
import com.reservationai.reservation.domain.RestaurantDetail;

import java.util.List;

public interface RetrieveRestaurantDomain {
    List<Restaurant> findByType(String categoryRestaurant, String cityRestaurant);
    List<Restaurant> findAllCategoriesByCity(String city);
    List<Restaurant> findAllNamesRestaurants();
    List<RestaurantDetail> findRestaurantByName(String name);
}
