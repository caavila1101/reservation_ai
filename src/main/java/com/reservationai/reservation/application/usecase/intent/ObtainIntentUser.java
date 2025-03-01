package com.reservationai.reservation.application.usecase.intent;

import com.reservationai.reservation.application.usecase.redirect.*;
import com.reservationai.reservation.domain.ports.AIService;
import com.reservationai.reservation.infrastructure.api.dto.OwnRestaurantDTO;
import com.reservationai.reservation.infrastructure.api.dto.RestaurantCommentsDTO;
import com.reservationai.reservation.infrastructure.api.dto.RestaurantDTO;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

@Service
public class ObtainIntentUser {

    private final RestaurantByCategoryRouter restaurantByCategoryRouter;
    private final RestaurantByNameRouter restaurantByNameRouter;
    private final CreateRestaurantRouter createRestaurantRouter;
    private final CreateOwnRestaurantRouter createOwnRestaurantRouter;
    private final CreateCommentRouter createCommentRouter;
    private final AIService aiService;

    public ObtainIntentUser(RestaurantByCategoryRouter restaurantByCategoryRouter, RestaurantByNameRouter restaurantByNameRouter, CreateRestaurantRouter createRestaurantRouter, CreateOwnRestaurantRouter createOwnRestaurantRouter, CreateCommentRouter createCommentRouter, AIService aiService) {
        this.restaurantByCategoryRouter = restaurantByCategoryRouter;
        this.restaurantByNameRouter = restaurantByNameRouter;
        this.createRestaurantRouter = createRestaurantRouter;
        this.createOwnRestaurantRouter = createOwnRestaurantRouter;
        this.createCommentRouter = createCommentRouter;
        this.aiService = aiService;
    }

    public String execute(String promptUser) {
        String promptTemplate = "Determina la intención del usuario y responde en este formato:\n" +
                "- Para buscar restaurantes por categoría y ciudad(Sí no encuentras el valor definciendo la categoria o la ciudad, usa un valor vacío en el espacio (\"\")): search_by_category|categoria, ciudad\n" +
                "- Para detalles de un restaurante(Sí no encuentras el valor de alguno, usa un valor vacío en el espacio (\"\")): search_by_name|nombreRestaurante\n" +
                "- Para crear un restaurante, (Sí no encuentras el valor de alguno, usa un valor vacío en el espacio (\"\")). Si no se detectan usuario y contraseña, usa valores vacíos (\"\") y (NO modifiques, corrijas, ni reformatees estos datos de ninguna forma. \\n\" +\n" +
                "  Mantenlos exactamente como fueron ingresados por el usuario, sin cambiar mayúsculas, acentos, espacios o cualquier otro carácter) \n" +
                "  create_restaurant|nombre-categoria-ciudad-direccion-descripcion-url-usuario-contraseña\n" +
                "- Para crear un usuario (NO modifiques, corrijas, ni reformatees estos datos de ninguna forma. \n" +
                "  Mantenlos exactamente como fueron ingresados por el usuario, sin cambiar mayúsculas, acentos, espacios o cualquier otro carácter)(Sí no encuentras el valor de alguno, usa un valor vacío en el espacio (\"\")) \n" +
                "  create_own_restaurant|usuario, email, contraseña\n" +
                "- Para crear un comentario sobre un resturante (Sí no encuentras el valor de alguno, usa un valor vacío en el espacio (\"\")): create_comment|nombreDelRestaurante-ciudadDelRestaurante-comentariosSobreElRestaurante-ratingDelRestaurante" +
                "Corrige errores, responde en minúsculas sin tildes y ajusta términos poco claros según el contexto.\n" +
                "Las ciudades ingresadas son de Colombia, por lo que puedes corregir errores de escritura para mejorar coincidencias.\n" +
                "Si la entrada no corresponde a ninguno de los formatos indicados, responde con: 0\n\n" +
                "Genera únicamente la respuesta en el formato exacto, sin agregar texto adicional, sin caracteres extras y sin modificar el nombre del restaurante.\n" +
                promptUser;
        String extractedInfo = aiService.createAnswer(promptTemplate).trim();

        if (extractedInfo.length() > 1){
            String[] parts = extractedInfo.split("\\|", 2);

            String intent = parts[0].trim();
            String data = parts[1].trim();

            return switch (intent) {
                case "search_by_category" -> handleCategorySearch(data);
                case "search_by_name" -> handleRestaurantDetails(data);
                case "create_restaurant" -> handleCreateRestaurant(data);
                case "create_own_restaurant" -> handleCreateOwnRestaurant(data);
                case "create_comment" -> handleCreateComment(data);
                default -> "";
            };
        }

        String prompt = "Dile que estas para ayudarlo a encontrar nuevos restaurantes para probar y brindarte información " +
                "sobre restaurantes específicos. Solo di esto de forma amigable, ya que es la primera vez que usan " +
                "la aplicación o quieren recordar en qué puedes ayudarles. Y que tu no puedes hacer nada externo al negocio, no saludes";
        return aiService.createAnswer(prompt);
    }

    private String handleCategorySearch(String data) {
        String[] parts = data.split(",", 2);
        String category = parts[0].trim();
        String city = parts.length > 1 ? parts[1].trim() : "0";

        return restaurantByCategoryRouter.searchRestaurantsByCity(category, city);
    }

    private String handleRestaurantDetails(String city) {
        return restaurantByNameRouter.searchRestaurantsByName(city);
    }

    //nombre, categoria, ciudad, direccion, descripcion, url
    private String handleCreateRestaurant(String data){
        String[] parts = data.split("-", 8);
        String name = parts[0].trim();
        String category = parts[1].trim();
        String city = parts[2].trim();
        String address = parts[3].trim();
        String description = parts[4].trim();
        String url = !parts[5].trim().isEmpty() ? parts[5].trim() : "";
        String user = !parts[6].trim().isEmpty() ? parts[6].trim() : "";
        String password = !parts[7].trim().isEmpty() ? parts[7].trim() : "";

        RestaurantDTO restaurantDTO = RestaurantDTO.builder()
                .name(name)
                .category(category)
                .city(city)
                .address(address)
                .description(description)
                .url(url)
                .user(user)
                .password(password)
                .build();

        return createRestaurantRouter.createRestaurant(restaurantDTO);
    }

    private String handleCreateOwnRestaurant(String data) {
        String[] parts = data.split(",", 3);
        String user = parts[0].trim();
        String email = parts[1].trim();
        String password = parts[2].trim();

        OwnRestaurantDTO ownRestaurantDTO = OwnRestaurantDTO.builder()
                .user(user)
                .email(email)
                .password(password)
                .build();

        return createOwnRestaurantRouter.createOwnRestaurant(ownRestaurantDTO);
    }

    private String handleCreateComment(String data) {
        String[] parts = data.split("-", 4);
        String nameRestaurant = parts[0].trim();
        String city = parts[1].trim();
        String comment = parts[2].trim();
        String rating = parts[3].trim();

        RestaurantCommentsDTO restaurantCommentsDTO = RestaurantCommentsDTO.builder()
                .cityRestaurant(city)
                .nameRestaurant(nameRestaurant)
                .comments(comment)
                .rating(rating)
                .build();

        return createCommentRouter.createCommentsRestaurant(restaurantCommentsDTO);
    }
}
