package com.reservationai.reservation.application.usecase;

import com.reservationai.reservation.domain.Restaurant;
import com.reservationai.reservation.domain.ports.AIService;
import com.reservationai.reservation.domain.ports.RetrieveRestaurantDomain;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRestaurants {

    private final RetrieveRestaurantDomain retrieveRestaurantDomain;
    private final AIService aiService;

    public GetRestaurants(RetrieveRestaurantDomain retrieveRestaurantDomain, AIService aiService) {
        this.retrieveRestaurantDomain = retrieveRestaurantDomain;
        this.aiService = aiService;
    }

    public String execute(String categoryRestaurant, String cityRestaurant) {

        List<String> categoriesByCity = getCategoriesByCity(cityRestaurant);

        if (!categoriesByCity.isEmpty()){
            String prompt = "Dada esta lista de categorías: " + categoriesByCity + " y esta categoría ingresada:" + categoryRestaurant +", encuentra la mejor coincidencia basada en similitud semántica o variaciones menores" +
                    "Si hay una coincidencia, responde solo con el nombre exacto de la categoría encontrada en la lista dada.  \n" +
                    "Si no hay coincidencias, responde solo con \"0\", sin añadir punto al final";

            String extractCategory = aiService.createAnswer(prompt).toLowerCase().trim();

            if (extractCategory.length() > 1){
                List<String> restaurants = getRestaurantsByType(extractCategory, cityRestaurant);

                if (restaurants.isEmpty()) {
                    String responsePrompt = "Informa de forma amigable que no encontramos restaurantes de categoría '"
                            + categoryRestaurant + "' en '" + cityRestaurant
                            + "'. Usa emojis.";
                    return aiService.createAnswer(responsePrompt);
                }

                String restaurantList = String.join(", ", restaurants);
                String responsePrompt = "Responde de forma amigable y breve, usando solo la información proporcionada y con emojis. Primero, menciona la lista de restaurantes disponibles sin describirlos. Luego, indica que si el usuario quiere más información sobre alguno en particular, puede mencionarlo. No agregues datos adicionales ni inventes información. Lista de restaurantes de "
                        + categoryRestaurant + " en " + cityRestaurant + ": " + restaurantList;
                return aiService.createAnswer(responsePrompt);
            }

            String promptNotFoundCategory = "Genera un mensaje amigable informando que no encontramos restaurantes de la categoría " + categoryRestaurant +
                    "' en '" + cityRestaurant +
                    ", pero que hay disponibles estas categorías: " + categoriesByCity + ", generame una respuesta no tan larga y no saludes.";
            return aiService.createAnswer(promptNotFoundCategory);
        }

        String promptNotFoundCity = "Informa de manera amigable que actualmente no tenemos restaurantes disponibles en " + cityRestaurant + ", generame una respuesta no tan larga  y no saludes.";
        return aiService.createAnswer(promptNotFoundCity);
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
