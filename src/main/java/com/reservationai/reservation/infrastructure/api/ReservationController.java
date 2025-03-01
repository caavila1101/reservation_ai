package com.reservationai.reservation.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reservationai.reservation.application.usecase.*;
import com.reservationai.reservation.application.usecase.intent.ObtainIntentUser;
import com.reservationai.reservation.infrastructure.api.dto.OwnRestaurantDTO;
import com.reservationai.reservation.infrastructure.api.dto.RestaurantCommentsDTO;
import com.reservationai.reservation.infrastructure.api.dto.RestaurantDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private final ObtainIntentUser obtainIntentUser;
    private final GetRestaurants getRestaurants;
    private final GetRestaurantByName getRestaurantByName;
    private final CreateResturant createResturant;
    private final CreateOwnRestaurant createOwnRestaurant;
    private final CreateOwnComment createOwnComment;

    public ReservationController(ObtainIntentUser obtainIntentUser, GetRestaurants getRestaurants, GetRestaurantByName getRestaurantByName, CreateResturant createResturant, CreateOwnRestaurant createOwnRestaurant, CreateOwnComment createOwnComment) {
        this.obtainIntentUser = obtainIntentUser;
        this.getRestaurants = getRestaurants;
        this.getRestaurantByName = getRestaurantByName;
        this.createResturant = createResturant;
        this.createOwnRestaurant = createOwnRestaurant;
        this.createOwnComment = createOwnComment;
    }

    @GetMapping("/user-intent")
    public String getIntentUser(@RequestParam String prompt){
        return obtainIntentUser.execute(prompt);
    }

    @GetMapping("/get-restaurants-by-category")
    public String getRestaurants(@RequestParam String category, @RequestParam String city) {
        return getRestaurants.execute(category, city);
    }

    @GetMapping("/get-restaurant-by-name")
    public String getRestaurantsByName(@RequestParam String name) {
        return getRestaurantByName.execute(name);
    }

    @PostMapping("/create-resturant")
    public String createRestaurant(@RequestBody RestaurantDTO restaurantDTO){
        return createResturant.execute(restaurantDTO);
    }

    @PostMapping("/create-own-restaurant")
    public String createOwnRestaurant(@RequestBody OwnRestaurantDTO ownRestaurantDTO){
        return createOwnRestaurant.execute(ownRestaurantDTO);
    }

    @PostMapping("/create-comment-restaurant")
    public String createOwnComment(@RequestBody RestaurantCommentsDTO restaurantCommentsDTO){
        return createOwnComment.execute(restaurantCommentsDTO);
    }

}
