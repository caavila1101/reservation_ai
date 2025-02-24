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

    private String extractRestaurantType(String extractedInfo) {
        List<String> knownTypes = List.of("comida rápida", "italiana");

        for (String type : knownTypes) {
            if (extractedInfo.toLowerCase().contains(type)) {
                return type;
            }
        }
        return null;
    }

    public String execute(String promptUser) {
        String prompt = "Extrae la intención en pocas palabras y el tipo de restaurante de esta consulta: " + promptUser;
        String extractedInfo = chatModel.call(prompt);

        String restaurantType = extractRestaurantType(extractedInfo);

        if (restaurantType != null) {
            List<String> restaurants = getRestaurantsByType(restaurantType);

            if (restaurants.isEmpty()) {
                return "No encontré restaurantes de " + restaurantType + " en Tunja.";
            }

            String restaurantList = String.join(", ", restaurants);
            String responsePrompt = "Responde de manera amigable, corta y no coloques definición a cada restaurante con esta lista de restaurantes de " + restaurantType + " en Tunja: " + restaurantList;
            return chatModel.call(new Prompt(
                    responsePrompt,
                    OpenAiChatOptions.builder()
                            .model("gpt-4o")
                            .temperature(0.2)
                            .build()
            )).getResult().getOutput().getText();
        }

        return "No entendí tu solicitud. ¿Puedes reformularla?";
    }

    private List<String> getRestaurantsByType(String restaurantType) {
        return retrieveRestaurantDomain.findByType(restaurantType)
                .stream()
                .map(Restaurant::getName)
                .toList();
    }
}
