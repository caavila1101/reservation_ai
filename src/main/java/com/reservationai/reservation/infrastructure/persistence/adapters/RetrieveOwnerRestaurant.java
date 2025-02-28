package com.reservationai.reservation.infrastructure.persistence.adapters;

import com.reservationai.reservation.domain.OwnRestaurant;
import com.reservationai.reservation.domain.ports.RetrieveOwnerRestaurantDomain;
import com.reservationai.reservation.infrastructure.persistence.entities.RestaurantOwnerEntity;
import com.reservationai.reservation.infrastructure.persistence.repository.OwnerRestaurantRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RetrieveOwnerRestaurant implements RetrieveOwnerRestaurantDomain {

    private OwnerRestaurantRepository ownerRestaurantRepository;

    public RetrieveOwnerRestaurant(OwnerRestaurantRepository ownerRestaurantRepository) {
        this.ownerRestaurantRepository = ownerRestaurantRepository;
    }

    @Override
    public List<OwnRestaurant> getOwnerByUser(String user) {
        return ownerRestaurantRepository.findByUser(user).stream().map(RestaurantOwnerEntity::toDomain).toList();
    }

    @Override
    public List<OwnRestaurant> getOwnerByEmail(String email) {
        return ownerRestaurantRepository.findByEmail(email).stream().map(RestaurantOwnerEntity::toDomain).toList();
    }

    @Override
    public List<OwnRestaurant> createOwner(OwnRestaurant ownRestaurant) {

        RestaurantOwnerEntity restaurantOwnerEntity = RestaurantOwnerEntity.builder()
                .id(ownRestaurant.getId())
                .user(ownRestaurant.getUser())
                .email(ownRestaurant.getEmail())
                .password(ownRestaurant.getPassword())
                .hasPremiumAccess(false)
                .createdAt(LocalDateTime.now())
                .build();

        return List.of(ownerRestaurantRepository.save(restaurantOwnerEntity).toDomain());
    }

    @Override
    public Boolean existsUser(String user, String password) {
        return ownerRestaurantRepository.existsByUserAndPassword(user, password);
    }
}
