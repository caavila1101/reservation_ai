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

    public String execute(String promptUser) {
        String prompt = "Extrae el tipo de restaurante y la ciudad de la consulta del usuario.  \n" +
                "Corrige errores ortográficos y devuelve ambos en minúsculas y sin tildes.  \n" +
                "Si el usuario escribe una categoría poco clara, intenta interpretarla según el contexto.  \n" +
                "Devuélvelos en el formato: categoria, ciudad, sin añadir punto al final.  \n" +
                "Si falta alguno, usa 0"
                + promptUser;
        String extractedInfo = chatModel.call(prompt);

        String[] parts = extractedInfo.split(",", 2);
        String categoryRestaurant = parts[0].trim();
        String cityRestaurant = parts.length > 1 ? parts[1].trim() : "";

        if(categoryRestaurant.length() > 1){
            if (cityRestaurant.length() > 1){
                List<String> restaurants = getRestaurantsByType(categoryRestaurant, cityRestaurant);

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
            }else{
                return "Por favor, dime en que ciudad quieres ver esta categoria de restaurante: " + categoryRestaurant;
            }
        }

        return "Por favor, dime qué tipo de restaurante buscas y en qué ciudad lo quieres ver \uD83D\uDE4F";
    }

    private List<String> getRestaurantsByType(String categoryRestaurant, String cityRestaurant) {
        return retrieveRestaurantDomain.findByType(categoryRestaurant, cityRestaurant)
                .stream()
                .map(Restaurant::getName)
                .toList();
    }
}
