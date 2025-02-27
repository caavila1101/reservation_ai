package com.reservationai.reservation.infrastructure.persistence.adapters;

import com.reservationai.reservation.domain.Restaurant;
import com.reservationai.reservation.domain.RestaurantDetail;
import com.reservationai.reservation.domain.dto.RestaurantDetailDomainDTO;
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
    public List<Restaurant> findRestaurantByNameAndCity(String name, String city) {
        var result = restaurantRepository.findByNameAndCity(name, city);
        return result.stream()
                .map(RestaurantEntity::toDomain)
                .toList();
    }

    @Override
    public List<Restaurant> createRestaurant(Restaurant restaurant) {
        RestaurantEntity restaurantEntity = RestaurantEntity.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .category(restaurant.getCategory())
                .city(restaurant.getCity())
                .createdAt(LocalDateTime.now())
                .build();

        return List.of(restaurantRepository.save(restaurantEntity).toDomain());
    }

    @Override
    public List<RestaurantDetail> createDetailRestaurant(RestaurantDetailDomainDTO restaurantDetailDomainDTO) {
        RestaurantDetailEntity restaurantDetail = RestaurantDetailEntity.builder()
                .id(restaurantDetailDomainDTO.getId())
                .name(restaurantDetailDomainDTO.getName())
                .address(restaurantDetailDomainDTO.getAddress())
                .description(restaurantDetailDomainDTO.getDescription())
                .url(restaurantDetailDomainDTO.getUrl())
                .city(restaurantDetailDomainDTO.getCity())
                .createdAt(LocalDateTime.now())
                .build();

        return List.of(restaurantDetailRepository.save(restaurantDetail).toDomain());
    }


}
