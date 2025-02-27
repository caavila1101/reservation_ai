package com.reservationai.reservation.infrastructure.persistence.repository;

import com.reservationai.reservation.infrastructure.persistence.entities.RestaurantOwnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnerRestaurantRepository extends JpaRepository<RestaurantOwnerEntity, String> {
    List<RestaurantOwnerEntity> findByUser(String user);
    List<RestaurantOwnerEntity> findByEmail(String email);

}
