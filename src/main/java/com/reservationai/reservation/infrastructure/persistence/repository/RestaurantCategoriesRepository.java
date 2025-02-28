package com.reservationai.reservation.infrastructure.persistence.repository;

import com.reservationai.reservation.infrastructure.persistence.entities.RestaurantsCategoriesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantCategoriesRepository extends JpaRepository<RestaurantsCategoriesEntity, String> {
    List<RestaurantsCategoriesEntity> findAll();
}
