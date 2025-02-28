package com.reservationai.reservation.application.usecase;

import com.reservationai.reservation.domain.Restaurant;
import com.reservationai.reservation.domain.RestaurantCategory;
import com.reservationai.reservation.domain.ports.AIService;
import com.reservationai.reservation.domain.ports.RetrieveRestaurantDomain;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GetRestaurants {

    private final RetrieveRestaurantDomain retrieveRestaurantDomain;
    private final AIService aiService;

    public GetRestaurants(RetrieveRestaurantDomain retrieveRestaurantDomain, AIService aiService) {
        this.retrieveRestaurantDomain = retrieveRestaurantDomain;
        this.aiService = aiService;
    }

    public String execute(String categoryRestaurant, String cityRestaurant) {

        List<String> allCategories = createDetailRestaurant();

        if(!allCategories.isEmpty()){
            List<String> categoriesByCity = getCategoriesByCity(cityRestaurant);

            if (!categoriesByCity.isEmpty()){

                String categoryPrompt = "Verifica si la categoría '" + categoryRestaurant + "' tiene alguna coincidencia razonable con las categorías disponibles en la siguiente lista: " +
                        String.join(", ", allCategories) + ".\n" +
                        "Devuelve únicamente el nombre de la categoría más similar si existe una coincidencia razonable, o una cadena vacía ('') si no hay coincidencia.\n" +
                        "⚠️ **Importante**: No debes corregir ni modificar la categoría ingresada. Compara de manera flexible, permitiendo algunas variaciones menores (como errores tipográficos o variaciones de nombre pequeñas), pero asegúrate de que la coincidencia sea razonable y no haga match con categorías completamente diferentes (por ejemplo, 'boyacenses' no debe coincidir con 'española'). Solo responde con la categoría más similar o una cadena vacía, sin punto final.";
                String extractCategory = aiService.createAnswer(categoryPrompt);

                boolean existsCategory = allCategories
                        .parallelStream()
                        .anyMatch(category -> category.toLowerCase()
                                .contains(extractCategory)
                        );

                if(!extractCategory.isEmpty() && existsCategory) {

                    List<String> restaurants = getRestaurantsByType(extractCategory, cityRestaurant);

                    if (restaurants.isEmpty()) {
                        String responsePrompt = "Informa de forma amigable que no encontramos restaurantes de categoría '"
                                + categoryRestaurant + "' en '" + cityRestaurant
                                + "'. Usa emojis.";
                        return aiService.createAnswer(responsePrompt);
                    }

                    String restaurantList = String.join(", ", restaurants);
                    String responsePrompt = "Responde de forma amigable y breve, usando solo la información proporcionada y con emojis. " +
                            "Primero, menciona la lista de restaurantes disponibles sin describirlos. " +
                            "Luego, indica que si el usuario quiere más información sobre alguno en particular, puede mencionarlo. " +
                            "No agregues datos adicionales ni inventes información. Lista de restaurantes de "
                            + categoryRestaurant + " en " + cityRestaurant + ": " + restaurantList;
                    return aiService.createAnswer(responsePrompt);

                }

                String responsePrompt =  "Dile la categoría ingresada por él: '" + categoryRestaurant + "'.\n\n" +
                        "e informa de manera amigable que no es válida.\n" +
                        "🔹 Luego, muestra la lista completa de categorías disponibles con emojis.\n\n" +
                        "Lista de categorías disponibles:\n" + String.join(", ", allCategories) + ".";
                return aiService.createAnswer(responsePrompt);


            }

            String promptNotFoundCity = "Informa de manera amigable que actualmente no tenemos restaurantes disponibles en " + cityRestaurant + ", generame una respuesta no tan larga  y no saludes.";
            return aiService.createAnswer(promptNotFoundCity);
        }

        String responsePrompt = "Informa al usuario de manera amigable que ocurrió un error inesperado y que intente nuevamente más tarde. " +
                "Usa un tono cordial y emojis para que el mensaje sea más amigable.";
        return aiService.createAnswer(responsePrompt);
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

    private List<String> createDetailRestaurant(){
        return retrieveRestaurantDomain
                .getAllCategories()
                .stream()
                .map(RestaurantCategory::getName)
                .collect(Collectors.toList());
    }
}
