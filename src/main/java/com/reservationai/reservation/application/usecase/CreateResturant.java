package com.reservationai.reservation.application.usecase;

import com.reservationai.reservation.domain.Restaurant;
import com.reservationai.reservation.domain.RestaurantDetail;
import com.reservationai.reservation.domain.dto.RestaurantDetailDomainDTO;
import com.reservationai.reservation.domain.ports.RetrieveRestaurantDomain;
import com.reservationai.reservation.infrastructure.api.dto.RestaurantDTO;
import jakarta.transaction.Transactional;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CreateResturant {

    private final ChatModel chatModel;
    private final RetrieveRestaurantDomain retrieveRestaurantDomain;

    public CreateResturant(ChatModel chatModel, RetrieveRestaurantDomain retrieveRestaurantDomain) {
        this.chatModel = chatModel;
        this.retrieveRestaurantDomain = retrieveRestaurantDomain;
    }

    @Transactional
    public String execute(RestaurantDTO restaurantDTO) {
        try {
            UUID uuid = UUID.randomUUID();
            String uuidRestaurant = uuid.toString();

            Restaurant restaurant = Restaurant.builder()
                    .id(uuidRestaurant)
                    .name(restaurantDTO.getName())
                    .category(restaurantDTO.getCategory())
                    .city(restaurantDTO.getCity())
                    .build();

            RestaurantDetailDomainDTO restaurantDetail = RestaurantDetailDomainDTO.builder()
                    .id(uuidRestaurant)
                    .name(restaurantDTO.getName())
                    .address(restaurantDTO.getAddress())
                    .description(restaurantDTO.getDescription())
                    .url(restaurantDTO.getUrl())
                    .city(restaurantDTO.getCity())
                    .build();

            List<Restaurant> restaurantExistsList =  getCategoriesByName(restaurant.getName(), restaurantDetail.getCity());

            if(restaurantExistsList.isEmpty()){
                List<Restaurant> restaurantList = createRestaurant(restaurant);
                List<RestaurantDetail> restaurantDetailList = createDetailRestaurant(restaurantDetail);

                if (!restaurantList.isEmpty() && !restaurantDetailList.isEmpty()) {
                    return chatModel.call(new Prompt(
                            "Confirma de manera amigable que el restaurante " + restaurantList.get(0).getName() + " fue creado exitosamente, utiliza emojis. Dandole bienvenida calurosa a la aplicacion llamada GastroGo",
                            OpenAiChatOptions.builder()
                                    .model("gpt-4o")
                                    .temperature(0.3)
                                    .build()
                    )).getResult().getOutput().getText();
                }
            }

            return chatModel.call(new Prompt(
                    "Aviso al usuario que el restaurante " + restaurant.getName() + " ubicado en " + restaurant.getCity() +
                            ", ya ha sido registrado previamente. Lamentablemente, no es posible crear un duplicado, utiliza emojis",
                    OpenAiChatOptions.builder()
                            .model("gpt-4o")
                            .temperature(0.3)
                            .build()
            )).getResult().getOutput().getText();

        }catch (Exception e){
            return chatModel.call("Explica de manera amigable que hubo un problema al crear el restaurante, sin dar detalles técnicos del error." + e.getMessage());
        }

    }

    private List<Restaurant> getCategoriesByName(String name, String city){
        return retrieveRestaurantDomain.findRestaurantByNameAndCity(name, city);
    }

    private List<Restaurant> createRestaurant(Restaurant restaurant){
        return retrieveRestaurantDomain.createRestaurant(restaurant);
    }

    private List<RestaurantDetail> createDetailRestaurant(RestaurantDetailDomainDTO restaurantDetail){
        return retrieveRestaurantDomain.createDetailRestaurant(restaurantDetail);
    }

}
