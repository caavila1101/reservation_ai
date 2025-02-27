package com.reservationai.reservation.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reservationai.reservation.application.usecase.CreateOwnRestaurant;
import com.reservationai.reservation.application.usecase.CreateResturant;
import com.reservationai.reservation.application.usecase.GetRestaurantByName;
import com.reservationai.reservation.application.usecase.GetRestaurants;
import com.reservationai.reservation.application.usecase.intent.ObtainIntentUser;
import com.reservationai.reservation.infrastructure.api.dto.OwnRestaurantDTO;
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

    public ReservationController(ObtainIntentUser obtainIntentUser, GetRestaurants getRestaurants, GetRestaurantByName getRestaurantByName, CreateResturant createResturant, CreateOwnRestaurant createOwnRestaurant) {
        this.obtainIntentUser = obtainIntentUser;
        this.getRestaurants = getRestaurants;
        this.getRestaurantByName = getRestaurantByName;
        this.createResturant = createResturant;
        this.createOwnRestaurant = createOwnRestaurant;
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

}
