package com.reservationai.reservation.infrastructure.persistence.entities;

import com.reservationai.reservation.domain.RestaurantDetail;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "restaurant_info")
@Getter
@Setter
public class RestaurantDetailEntity {
    @Id
    private String id;

    private String name;
    private String address;
    private String description;
    private String url;
    private String city;
    private String userOwner;
    @Column(updatable = false)
    private LocalDateTime createdAt;

    public RestaurantDetail toDomain(){
        return RestaurantDetail.builder()
                .id(this.id)
                .name(this.name)
                .address(this.address)
                .description(this.description)
                .url(this.url)
                .city(this.city)
                .createdAt(createdAt)
                .build();
    }
}
