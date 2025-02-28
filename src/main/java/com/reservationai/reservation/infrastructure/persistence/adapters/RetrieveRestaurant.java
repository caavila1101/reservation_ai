package com.reservationai.reservation.infrastructure.persistence.adapters;

import com.reservationai.reservation.domain.Restaurant;
import com.reservationai.reservation.domain.RestaurantDetail;
import com.reservationai.reservation.domain.ports.RetrieveRestaurantDomain;
import com.reservationai.reservation.infrastructure.persistence.entities.RestaurantDetailEntity;
import com.reservationai.reservation.infrastructure.persistence.entities.RestaurantEntity;
import com.reservationai.reservation.infrastructure.persistence.repository.RestaurantDetailRepository;
import com.reservationai.reservation.infrastructure.persistence.repository.RestaurantRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
                .map(RestaurantEntity::toDomain)
                .toList();
    }

    @Override
    public List<Restaurant> findAllCategoriesByCity(String city) {
        var result = restaurantRepository.findDistinctByCity(city);
        return result.stream()
                .map(RestaurantEntity::toDomain)
                .toList();
    }

    @Override
    public List<Restaurant> findAllNamesRestaurants() {
        return restaurantRepository.findAllBy()
                .stream()
                .map(RestaurantEntity::toDomain)
                .toList();
    }

    @Override
    public List<RestaurantDetail> findRestaurantByName(String name) {
        var result = restaurantDetailRepository.findByName(name);
        return result.stream()
                .map(RestaurantDetailEntity::toDomain)
                .toList();
    }

    @Override
    public Boolean findRestaurantByNameAndCity(String name, String city) {
        return restaurantRepository.existsByNameAndCity(name, city);
    }

    @Override
    public List<Restaurant> createRestaurant(Restaurant restaurant) {
        RestaurantEntity restaurantEntity = RestaurantEntity.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .category(restaurant.getCategory())
                .city(restaurant.getCity())
                .userOwner(restaurant.getUserOwner())
                .createdAt(LocalDateTime.now())
                .build();

        return List.of(restaurantRepository.save(restaurantEntity).toDomain());
    }

    @Override
    public List<RestaurantDetail> createDetailRestaurant(RestaurantDetail restaurantDetailInput) {
        RestaurantDetailEntity restaurantDetail = RestaurantDetailEntity.builder()
                .id(restaurantDetailInput.getId())
                .name(restaurantDetailInput.getName())
                .address(restaurantDetailInput.getAddress())
                .description(restaurantDetailInput.getDescription())
                .url(restaurantDetailInput.getUrl())
                .city(restaurantDetailInput.getCity())
                .userOwner(restaurantDetailInput.getUserOwner())
                .createdAt(LocalDateTime.now())
                .build();

        return List.of(restaurantDetailRepository.save(restaurantDetail).toDomain());
    }


}
