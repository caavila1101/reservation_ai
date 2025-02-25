package com.reservationai.reservation.infrastructure.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.reservationai.reservation.application.usecase.GetRestaurants;
import com.reservationai.reservation.application.usecase.intent.ObtainIntentUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservation")
public class ReservationController {

    private ObtainIntentUser obtainIntentUser;
    private final GetRestaurants getRestaurants;

    public ReservationController(GetRestaurants getRestaurants, ObtainIntentUser obtainIntentUser) {
        this.getRestaurants = getRestaurants;
        this.obtainIntentUser = obtainIntentUser;
    }

    @GetMapping("/user-intent")
    public String getIntentUser(@RequestParam String prompt){
        return obtainIntentUser.execute(prompt);
    }

    @GetMapping("/get-restaurants-by-category")
    public String getRestaurants(@RequestParam String category, @RequestParam String city) {
        return getRestaurants.execute(category, city);
    }

}
