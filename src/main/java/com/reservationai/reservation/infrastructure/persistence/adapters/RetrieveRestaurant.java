package com.reservationai.reservation.infrastructure.persistence.adapters;

import com.reservationai.reservation.domain.Restaurant;
import com.reservationai.reservation.domain.RestaurantDetail;
import com.reservationai.reservation.domain.ports.RetrieveRestaurantDomain;
import com.reservationai.reservation.infrastructure.persistence.entities.RestaurantDetailEntity;
import com.reservationai.reservation.infrastructure.persistence.entities.RestaurantEntity;
import com.reservationai.reservation.infrastructure.persistence.repository.RestaurantDetailRepository;
import com.reservationai.reservation.infrastructure.persistence.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RetrieveRestaurant implements RetrieveRestaurantDomain {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantDetailRepository restaurantDetailRepository;

    public RetrieveRestaurant(RestaurantRepository restaurantRepository, RestaurantDetailRepository restaurantDetailRepository) {
        this.restaurantRepository = restaurantRepository;
        this.restaurantDetailRepository = restaurantDetailRepository;
    }

    @Override
    public List<Restaurant> findByType(String categoryRestaurant, String cityRestaurant) {
        var result = restaurantRepository.findByCategoryAndCity(categoryRestaurant, cityRestaurant);
        return result.stream()
                .map(RestaurantEntity::toEntity)
                .toList();
    }

    @Override
    public List<Restaurant> findAllCategoriesByCity(String city) {
        var result = restaurantRepository.findDistinctByCity(city);
        return result.stream()
                .map(RestaurantEntity::toEntity)
                .toList();
    }

    @Override
    public List<Restaurant> findAllNamesRestaurants() {
        return restaurantRepository.findAllBy()
                .stream()
                .map(RestaurantEntity::toEntity)
                .toList();
    }

    @Override
    public List<RestaurantDetail> findRestaurantByName(String name) {
        var result = restaurantDetailRepository.findByName(name);
        return result.stream()
                .map(RestaurantDetailEntity::toEntity)
                .toList();
    }
}
