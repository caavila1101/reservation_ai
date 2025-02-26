package com.reservationai.reservation.infrastructure.persistence.repository;

import com.reservationai.reservation.infrastructure.persistence.entities.RestaurantDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RestaurantDetailRepository extends JpaRepository<RestaurantDetailEntity, Long> {
    List<RestaurantDetailEntity> findByName(String name);
}
