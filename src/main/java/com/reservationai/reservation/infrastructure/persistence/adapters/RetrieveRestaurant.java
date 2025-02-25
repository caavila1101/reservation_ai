package com.reservationai.reservation.infrastructure.persistence.adapters;

import com.reservationai.reservation.domain.Restaurant;
import com.reservationai.reservation.domain.ports.RetrieveRestaurantDomain;
import com.reservationai.reservation.infrastructure.persistence.entities.RestaurantEntity;
import com.reservationai.reservation.infrastructure.persistence.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RetrieveRestaurant implements RetrieveRestaurantDomain {

    private final RestaurantRepository restaurantRepository;

    public RetrieveRestaurant(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public List<Restaurant> findByType(String categoryRestaurant, String cityRestaurant) {
        var result = restaurantRepository.findByCategoryAndCity(categoryRestaurant, cityRestaurant);
        return result.stream()
                .map(RestaurantEntity::toEntity)
                .toList();
    }
}
