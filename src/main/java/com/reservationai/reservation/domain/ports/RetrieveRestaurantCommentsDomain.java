package com.reservationai.reservation.domain.ports;

import com.reservationai.reservation.domain.RestaurantComments;

import java.util.List;

public interface RetrieveRestaurantCommentsDomain {
    List<RestaurantComments> saveComment(RestaurantComments restaurantComments);
}
