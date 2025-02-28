package com.reservationai.reservation.application.usecase;

import com.reservationai.reservation.domain.Restaurant;
import com.reservationai.reservation.domain.RestaurantCategory;
import com.reservationai.reservation.domain.RestaurantDetail;
import com.reservationai.reservation.domain.ports.AIService;
import com.reservationai.reservation.domain.ports.RetrieveOwnerRestaurantDomain;
import com.reservationai.reservation.domain.ports.RetrieveRestaurantDomain;
import com.reservationai.reservation.infrastructure.api.dto.RestaurantDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CreateResturant {

    private final RetrieveRestaurantDomain retrieveRestaurantDomain;
    private final RetrieveOwnerRestaurantDomain retrieveOwnerRestaurantDomain;
    private final AIService aiService;

    public CreateResturant(RetrieveRestaurantDomain retrieveRestaurantDomain, RetrieveOwnerRestaurantDomain retrieveOwnerRestaurantDomain, AIService aiService) {
        this.retrieveRestaurantDomain = retrieveRestaurantDomain;
        this.retrieveOwnerRestaurantDomain = retrieveOwnerRestaurantDomain;
        this.aiService = aiService;
    }

    @Transactional
    public String execute(RestaurantDTO restaurantDTO) {
        try {
            if(!restaurantDTO.getUser().isEmpty() && !restaurantDTO.getPassword().isEmpty()){

                Boolean existUser = getOwnerByUserAndPassword(restaurantDTO.getUser(), DigestUtils.sha256Hex(restaurantDTO.getPassword()));

                if(existUser){

                    List<String> allCategories = createDetailRestaurant();

                    if(!allCategories.isEmpty()){

                        String categoryPrompt = "Verifica si la categoría '" + restaurantDTO.getCategory() + "' tiene alguna coincidencia razonable con las categorías disponibles en la siguiente lista: " +
                                String.join(", ", allCategories) + ".\n" +
                                "Devuelve únicamente el nombre de la categoría más similar si existe una coincidencia razonable, o una cadena vacía ('') si no hay coincidencia.\n" +
                                "⚠️ **Importante**: No debes corregir ni modificar la categoría ingresada. Compara de manera flexible, permitiendo algunas variaciones menores (como errores tipográficos o variaciones de nombre pequeñas), pero asegúrate de que la coincidencia sea razonable y no haga match con categorías completamente diferentes (por ejemplo, 'boyacenses' no debe coincidir con 'española'). Solo responde con la categoría más similar o una cadena vacía, sin punto final.";
                        String extractCategory = aiService.createAnswer(categoryPrompt);

                        boolean existsCategory = allCategories
                                .parallelStream()
                                .anyMatch(category -> category.toLowerCase()
                                        .contains(extractCategory)
                                );

                        if(!extractCategory.isEmpty() && existsCategory){
                            UUID uuid = UUID.randomUUID();
                            String uuidRestaurant = uuid.toString();

                            Restaurant restaurant = Restaurant.builder()
                                    .id(uuidRestaurant)
                                    .name(restaurantDTO.getName())
                                    .category(extractCategory)
                                    .city(restaurantDTO.getCity())
                                    .userOwner(restaurantDTO.getUser())
                                    .build();

                            RestaurantDetail restaurantDetail = RestaurantDetail.builder()
                                    .id(uuidRestaurant)
                                    .name(restaurantDTO.getName())
                                    .address(restaurantDTO.getAddress())
                                    .description(restaurantDTO.getDescription())
                                    .url(restaurantDTO.getUrl())
                                    .city(restaurantDTO.getCity())
                                    .userOwner(restaurantDTO.getUser())
                                    .build();

                            Boolean restaurantExistsList =  getCategoriesByName(restaurant.getName(), restaurantDetail.getCity());

                            if(!restaurantExistsList){
                                List<Restaurant> restaurantList = createRestaurant(restaurant);
                                List<RestaurantDetail> restaurantDetailList = createDetailRestaurant(restaurantDetail);

                                if (!restaurantList.isEmpty() && !restaurantDetailList.isEmpty()) {
                                    String responsePrompt = "Confirma de manera amigable que el restaurante " + restaurantList.get(0).getName() + " fue creado exitosamente, utiliza emojis. " +
                                            "Dandole bienvenida calurosa a la aplicacion llamada GastroGo";
                                    return aiService.createAnswer(responsePrompt);
                                }
                            }

                            String responsePrompt  = "Aviso al usuario que el restaurante " + restaurant.getName() + " ubicado en " + restaurant.getCity() +
                                    ", ya ha sido registrado previamente. Lamentablemente, no es posible crear un duplicado, utiliza emojis";
                            return aiService.createAnswer(responsePrompt);
                        }

                        String responsePrompt =  "Dile la categoría ingresada por él: '" + restaurantDTO.getCategory() + "'.\n\n" +
                                "e informa de manera amigable que no es válida.\n" +
                                "🔹 Luego, muestra la lista completa de categorías disponibles con emojis.\n\n" +
                                "Lista de categorías disponibles:\n" + String.join(", ", allCategories) + ".";
                        return aiService.createAnswer(responsePrompt);


                    }

                    String responsePrompt = "Informa al usuario de manera amigable que ocurrió un error inesperado y que intente nuevamente más tarde. " +
                            "Usa un tono cordial y emojis para que el mensaje sea más amigable.";
                    return aiService.createAnswer(responsePrompt);
                }

                ///ARREGLAR ESTE PROMPT, ME DEVULVE MUCHA INFORMACION INNECESARIA
                String prompt = "Antes de crear un restaurante, verifica primero si las credenciales ingresadas son correctas. 🔍\n\n" +
                        "Si las credenciales no coinciden con un usuario registrado, por favor revisa tus datos e inténtalo nuevamente. 🚫🔄\n\n" +
                        "Si aún no tienes un usuario, debes registrarte proporcionando los siguientes datos obligatorios:\n" +
                        "- 🆔 Usuario: Un nombre único para identificarte.\n" +
                        "- 📧 Email: Una dirección de correo electrónico válida.\n" +
                        "- 🔑 Contraseña: Una clave segura para acceder a tu cuenta.\n\n" +
                        "Una vez registrado, podrás proceder a crear tu restaurante. 🍽️";
                return aiService.createAnswer(prompt);

            }

            String prompt = "Para crear un restaurante, es obligatorio ingresar un usuario y una contraseña.\n" +
                    "Si el usuario no proporciona estos datos, responde directamente con el siguiente mensaje sin agregar introducciones:\n\n" +
                    "\"🚀 Para continuar con la creación de tu restaurante, necesitamos que ingreses un usuario y una contraseña. 🔑 " +
                    "Por favor, proporciona estos datos para seguir adelante. ¡Gracias! 😊\"\n\n" +
                    "Asegúrate de que el mensaje sea claro y amigable, usando emojis.";
            return aiService.createAnswer(prompt);

        }catch (Exception e){
            String prompt = "Explica de manera amigable que hubo un problema al crear el restaurante, sin dar detalles técnicos del error." + e.getMessage();
            return aiService.createAnswer(prompt);
        }

    }

    private Boolean getOwnerByUserAndPassword(String user, String password){
        return retrieveOwnerRestaurantDomain.existsUser(user, password);
    }

    private Boolean getCategoriesByName(String name, String city){
        return retrieveRestaurantDomain.findRestaurantByNameAndCity(name, city);
    }

    private List<Restaurant> createRestaurant(Restaurant restaurant){
        return retrieveRestaurantDomain.createRestaurant(restaurant);
    }

    private List<RestaurantDetail> createDetailRestaurant(RestaurantDetail restaurantDetail){
        return retrieveRestaurantDomain.createDetailRestaurant(restaurantDetail);
    }

    private List<String> createDetailRestaurant(){
        return retrieveRestaurantDomain
                .getAllCategories()
                .stream()
                .map(RestaurantCategory::getName)
                .collect(Collectors.toList());
    }

}
