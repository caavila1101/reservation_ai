package com.reservationai.reservation.application.usecase.intent;

import com.reservationai.reservation.application.usecase.redirect.CreateRestaurantRouter;
import com.reservationai.reservation.application.usecase.redirect.RestaurantByCategoryRouter;
import com.reservationai.reservation.application.usecase.redirect.RestaurantByNameRouter;
import com.reservationai.reservation.infrastructure.api.dto.RestaurantDTO;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class ObtainIntentUser {

    private final ChatModel chatModel;
    private final RestaurantByCategoryRouter restaurantByCategoryRouter;
    private final RestaurantByNameRouter restaurantByNameRouter;
    private final CreateRestaurantRouter createRestaurantRouter;

    public ObtainIntentUser(ChatModel chatModel, RestaurantByCategoryRouter restaurantByCategoryRouter, RestaurantByNameRouter restaurantByNameRouter, CreateRestaurantRouter createRestaurantRouter) {
        this.chatModel = chatModel;
        this.restaurantByCategoryRouter = restaurantByCategoryRouter;
        this.restaurantByNameRouter = restaurantByNameRouter;
        this.createRestaurantRouter = createRestaurantRouter;
    }

    public String execute(String promptUser) {
        String promptTemplate = "Determina la intención del usuario y responde en este formato:\n" +
                "- Para buscar restaurantes por categoría y ciudad: search_by_category|categoria, ciudad\n" +
                "- Para detalles de un restaurante: search_by_name|nombreRestaurante\n" +
                "- Para crear un resturante: create_restaurant|nombre-categoria-ciudad-direccion-descripcion-url" +
                "Corrige errores, responde en minúsculas sin tildes y ajusta términos poco claros según el contexto.\n" +
                "Las ciudades ingresadas son de Colombia, por lo que puedes corregir errores de escritura para mejorar coincidencias sin añadir punto al final.\n" +
                "Si la entrada no corresponde a ninguno de los formatos indicados, responde con: 0.\n\n" +
                promptUser;

        String extractedInfo = chatModel.call(promptTemplate).toLowerCase().trim();

        if (extractedInfo.length() > 1){
            String[] parts = extractedInfo.split("\\|", 2);

            String intent = parts[0].trim();
            String data = parts[1].trim();

            return switch (intent) {
                case "search_by_category" -> handleCategorySearch(data);
                case "search_by_name" -> handleRestaurantDetails(data);
                case "create_restaurant" -> handleCreateRestaurant(data);
                default -> "";
            };
        }

        String prompt = "Dile que estas para ayudarlo a encontrar nuevos restaurantes para probar y brindarte información " +
                "sobre restaurantes específicos. Solo di esto de forma amigable, ya que es la primera vez que usan " +
                "la aplicación o quieren recordar en qué puedes ayudarles. Y que tu no puedes hacer nada externo al negocio, no saludes";
        return chatModel.call(prompt);
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

    //nombre, categoria, ciudad, direccion, descripcion, url
    private String handleCreateRestaurant(String data){
        String[] parts = data.split("-", 6);
        String name = parts[0].trim();
        String category = parts[1].trim();
        String city = parts[2].trim();
        String address = parts[3].trim();
        String description = parts[4].trim();
        String url = (parts.length > 5 && !parts[5].trim().isEmpty()) ? parts[5].trim() : "";

        RestaurantDTO restaurantDTO = RestaurantDTO.builder()
                .name(name)
                .category(category)
                .city(city)
                .address(address)
                .description(description)
                .url(url)
                .build();

        return createRestaurantRouter.createRestaurant(restaurantDTO);
    }
}
