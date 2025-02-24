package com.reservationai.reservation.infrastructure.api;

import com.reservationai.reservation.application.usecase.GetRestaurants;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReservationController {

    private final GetRestaurants getRestaurants;

    public ReservationController(GetRestaurants getRestaurants) {
        this.getRestaurants = getRestaurants;
    }

    @GetMapping
    public String health(){
        return "ESTA FUNCIONANDO";
    }

    @GetMapping("/get-restaurants")
    public String getRestaurants(@RequestParam String prompt) {
        return getRestaurants.execute(prompt);
    }


}
