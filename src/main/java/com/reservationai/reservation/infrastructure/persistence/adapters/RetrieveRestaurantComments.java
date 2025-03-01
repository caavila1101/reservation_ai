package com.reservationai.reservation.infrastructure.persistence.adapters;

import com.reservationai.reservation.domain.RestaurantComments;
import com.reservationai.reservation.domain.ports.RetrieveRestaurantCommentsDomain;
import com.reservationai.reservation.infrastructure.persistence.entities.RestaurantReviewsEntity;
import com.reservationai.reservation.infrastructure.persistence.repository.RestaurantCommentsRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class RetrieveRestaurantComments implements RetrieveRestaurantCommentsDomain {

    private final RestaurantCommentsRepository restaurantCommentsRepository;

    public RetrieveRestaurantComments(RestaurantCommentsRepository restaurantCommentsRepository) {
        this.restaurantCommentsRepository = restaurantCommentsRepository;
    }

    @Override
    public List<RestaurantComments> saveComment(RestaurantComments restaurantComments) {
        UUID uuid = UUID.randomUUID();

        RestaurantReviewsEntity reviewsEntity = RestaurantReviewsEntity.builder()
                .id(uuid.toString())
                .nameRestaurant(restaurantComments.getNameRestaurant())
                .restaurantCity(restaurantComments.getCityRestaurant())
                .comment(restaurantComments.getComments())
                .rating(restaurantComments.getRating())
                .createdAt(LocalDateTime.now())
                .build();

        return List.of(restaurantCommentsRepository.save(reviewsEntity).toDomain());
    }
}
