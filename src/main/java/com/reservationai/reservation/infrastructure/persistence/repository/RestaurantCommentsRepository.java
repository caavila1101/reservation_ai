package com.reservationai.reservation.infrastructure.persistence.repository;

import com.reservationai.reservation.infrastructure.persistence.entities.RestaurantReviewsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantCommentsRepository extends JpaRepository<RestaurantReviewsEntity, String> {
}
