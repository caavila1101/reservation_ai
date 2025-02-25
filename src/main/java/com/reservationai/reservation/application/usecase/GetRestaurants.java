package com.reservationai.reservation.application.usecase;

import com.reservationai.reservation.domain.Restaurant;
import com.reservationai.reservation.domain.ports.RetrieveRestaurantDomain;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRestaurants {

    private final ChatModel chatModel;
    private final RetrieveRestaurantDomain retrieveRestaurantDomain;

    public GetRestaurants(ChatModel chatModel, RetrieveRestaurantDomain retrieveRestaurantDomain) {
        this.chatModel = chatModel;
        this.retrieveRestaurantDomain = retrieveRestaurantDomain;
    }

    public String execute(String categoryRestaurant, String cityRestaurant) {

        List<String> categoriesByCity = getCategoriesByCity(cityRestaurant);

        if (!categoriesByCity.isEmpty()){
            String prompt = "Dada esta lista de categorías: " + categoriesByCity + " y esta categoría ingresada:" + categoryRestaurant +", encuentra la mejor coincidencia basada en similitud semántica o variaciones menores" +
                    "Si hay una coincidencia, responde solo con el nombre exacto de la categoría encontrada en la lista dada.  \n" +
                    "Si no hay coincidencias, responde solo con \"0\", sin añadir punto al final";
            String extractCategory = chatModel.call(prompt).toLowerCase().trim();

            if (extractCategory.length() > 1){
                List<String> restaurants = getRestaurantsByType(extractCategory, cityRestaurant);

                if (restaurants.isEmpty()) {
                    return "No encontré restaurantes de categoria " + categoryRestaurant + " en " + cityRestaurant;
                }

                String restaurantList = String.join(", ", restaurants);
                String responsePrompt = "Responde de forma amigable y breve, usando únicamente la información dada. No agregues datos extra ni describas cada restaurante. Lista de restaurantes de "
                        + categoryRestaurant + " en " + cityRestaurant + ": " + restaurantList;

                return chatModel.call(new Prompt(
                        responsePrompt,
                        OpenAiChatOptions.builder()
                                .model("gpt-4o")
                                .temperature(0.2)
                                .build()
                )).getResult().getOutput().getText();
            }

            String promptNotFoundCategory = "Genera un mensaje amigable informando que no encontramos restaurantes de la categoría " + categoryRestaurant +
                    "' en '" + cityRestaurant +
                    ", pero que hay disponibles estas categorías: " + categoriesByCity + ", generame una respuesta no tan larga y no saludes.";
            return chatModel.call(
                    new Prompt(
                            promptNotFoundCategory,
                            OpenAiChatOptions.builder()
                                    .model("gpt-4o")
                                    .temperature(0.2)
                                    .build()
                    )
            ).getResult().getOutput().getText();
        }

        String promptNotFoundCity = "Informa de manera amigable que actualmente no tenemos restaurantes disponibles en " + cityRestaurant + ", generame una respuesta no tan larga  y no saludes.";
        return chatModel.call(
                new Prompt(
                        promptNotFoundCity,
                        OpenAiChatOptions.builder()
                                .model("gpt-4o")
                                .temperature(0.2)
                                .build()
                )
        ).getResult().getOutput().getText();

    }

    private List<String> getRestaurantsByType(String categoryRestaurant, String cityRestaurant) {
        return retrieveRestaurantDomain.findByType(categoryRestaurant, cityRestaurant)
                .stream()
                .map(Restaurant::getName)
                .toList();
    }

    private List<String> getCategoriesByCity(String city){
        return retrieveRestaurantDomain.findAllCategoriesByCity(city)
                .stream()
                .map(Restaurant::getCategory)
                .toList();
    }
}
