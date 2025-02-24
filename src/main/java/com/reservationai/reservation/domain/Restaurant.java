package com.reservationai.reservation.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
public class Restaurant {
    private Long id;
    private String name;
    private String type;
    private String address;

    public Restaurant(Long id, String name, String type, String address) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
