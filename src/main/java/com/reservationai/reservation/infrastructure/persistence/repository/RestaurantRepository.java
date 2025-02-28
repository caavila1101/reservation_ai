package com.reservationai.reservation.infrastructure.persistence.repository;

import com.reservationai.reservation.infrastructure.persistence.entities.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantRepository extends JpaRepository<RestaurantEntity, String> {
    List<RestaurantEntity> findByCategoryAndCity(String categoryRestaurant, String cityRestaurant);
    List<RestaurantEntity> findDistinctByCity(String city);
    List<RestaurantEntity> findAllBy();
    Boolean existsByNameAndCity(String name, String city);
}
