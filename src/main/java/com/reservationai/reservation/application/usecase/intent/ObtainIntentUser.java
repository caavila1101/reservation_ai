package com.reservationai.reservation.application.usecase.intent;

import com.reservationai.reservation.application.usecase.redirect.RestaurantByCategoryRouter;
import com.reservationai.reservation.application.usecase.redirect.RestaurantByNameRouter;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class ObtainIntentUser {

    private final ChatModel chatModel;
    private final RestaurantByCategoryRouter restaurantByCategoryRouter;
    private final RestaurantByNameRouter restaurantByNameRouter;

    public ObtainIntentUser(ChatModel chatModel, RestaurantByCategoryRouter restaurantByCategoryRouter, RestaurantByNameRouter restaurantByNameRouter) {
        this.chatModel = chatModel;
        this.restaurantByCategoryRouter = restaurantByCategoryRouter;
        this.restaurantByNameRouter = restaurantByNameRouter;
    }

    public String execute(String promptUser) {
        String promptTemplate = "Determina la intención del usuario y responde en este formato:\n" +
                "- Para buscar restaurantes por categoría y ciudad: search_by_category|categoria, ciudad.\n" +
                "- Para detalles de un restaurante: search_by_name|nombreRestaurante.\n" +
                "Corrige errores, responde en minúsculas sin tildes y ajusta términos poco claros según el contexto.\n" +
                "Las ciudades ingresadas son de Colombia, por lo que puedes corregir errores de escritura para mejorar coincidencias sin añadir punto al final: " +
                promptUser;

        String extractedInfo = chatModel.call(promptTemplate).toLowerCase().trim();

        String[] parts = extractedInfo.split("\\|", 2);
        if (parts.length < 2) return "No entendí tu solicitud, ¿puedes reformularla?";

        String intent = parts[0].trim();
        String data = parts[1].trim();

        switch (intent) {
            case "search_by_category":
                return handleCategorySearch(data);
            case "search_by_name":
                return handleRestaurantDetails(data);
            default:
                return "No entendí tu solicitud, ¿puedes reformularla?";
        }
    }

    private String handleCategorySearch(String data) {
        String[] parts = data.split(",", 2);
        String category = parts[0].trim();
        String city = parts.length > 1 ? parts[1].trim() : "0";

        if (category.equals("0")) return "Por favor, dime qué tipo de restaurante buscas.";
        if (city.equals("0")) return "Por favor, dime en qué ciudad quieres buscar.";

        return restaurantByCategoryRouter.searchRestaurantsByCity(category, city);
    }

    private String handleRestaurantDetails(String city) {
        return restaurantByNameRouter.searchRestaurantsByName(city);
    }
}
