package com.reservationai.reservation.domain;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class Restaurant {

    private String id;
    private String name;
    private String category;
    private String city;
    private String userOwner;
    private LocalDateTime createdAt;

}
