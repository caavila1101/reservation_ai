package com.reservationai.reservation.domain.ports;

import com.reservationai.reservation.domain.OwnRestaurant;

import java.util.List;

public interface RetrieveOwnerRestaurantDomain {
    List<OwnRestaurant> getOwnerByUser(String user);
    List<OwnRestaurant> getOwnerByEmail(String email);
    List<OwnRestaurant> createOwner(OwnRestaurant ownRestaurant);
}
