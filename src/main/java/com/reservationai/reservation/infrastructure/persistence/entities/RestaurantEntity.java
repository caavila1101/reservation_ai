package com.reservationai.reservation.infrastructure.persistence.entities;

import com.reservationai.reservation.domain.Restaurant;
import jakarta.persistence.*;

@Entity
@Table(name = "restaurants")
public class RestaurantEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private String address;

    public RestaurantEntity() {}

    public RestaurantEntity(String name, String type, String address) {
        this.name = name;
        this.type = type;
        this.address = address;
    }

    public Restaurant toEntity(){
        return Restaurant.builder()
                .id(this.id)
                .name(this.name)
                .type(this.type)
                .address(this.address)
                .build();
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
