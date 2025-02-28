package com.reservationai.reservation.application.usecase;

import com.reservationai.reservation.domain.OwnRestaurant;
import com.reservationai.reservation.domain.Restaurant;
import com.reservationai.reservation.domain.RestaurantDetail;
import com.reservationai.reservation.domain.ports.RetrieveOwnerRestaurantDomain;
import com.reservationai.reservation.domain.ports.RetrieveRestaurantDomain;
import com.reservationai.reservation.infrastructure.api.dto.RestaurantDTO;
import jakarta.transaction.Transactional;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.List;
import java.util.UUID;

@Service
public class CreateResturant {

    private final ChatModel chatModel;
    private final RetrieveRestaurantDomain retrieveRestaurantDomain;
    private final RetrieveOwnerRestaurantDomain retrieveOwnerRestaurantDomain;

    public CreateResturant(ChatModel chatModel, RetrieveRestaurantDomain retrieveRestaurantDomain, RetrieveOwnerRestaurantDomain retrieveOwnerRestaurantDomain) {
        this.chatModel = chatModel;
        this.retrieveRestaurantDomain = retrieveRestaurantDomain;
        this.retrieveOwnerRestaurantDomain = retrieveOwnerRestaurantDomain;
    }

    @Transactional
    public String execute(RestaurantDTO restaurantDTO) {
        try {

            if(!restaurantDTO.getUser().isEmpty() && !restaurantDTO.getPassword().isEmpty()){

                Boolean existUser = getOwnerByUserAndPassword(restaurantDTO.getUser(), DigestUtils.sha256Hex(restaurantDTO.getPassword()));

                if(existUser){
                    UUID uuid = UUID.randomUUID();
                    String uuidRestaurant = uuid.toString();

                    Restaurant restaurant = Restaurant.builder()
                            .id(uuidRestaurant)
                            .name(restaurantDTO.getName())
                            .category(restaurantDTO.getCategory())
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
                }

                String prompt = "Antes de crear un restaurante, verifica primero si las credenciales ingresadas son correctas. 🔍\n\n" +
                        "Si las credenciales no coinciden con un usuario registrado, por favor revisa tus datos e inténtalo nuevamente. 🚫🔄\n\n" +
                        "Si aún no tienes un usuario, debes registrarte proporcionando los siguientes datos obligatorios:\n" +
                        "- 🆔 Usuario: Un nombre único para identificarte.\n" +
                        "- 📧 Email: Una dirección de correo electrónico válida.\n" +
                        "- 🔑 Contraseña: Una clave segura para acceder a tu cuenta.\n\n" +
                        "Una vez registrado, podrás proceder a crear tu restaurante. 🍽️";
                return chatModel.call(new Prompt(
                        prompt,
                        OpenAiChatOptions.builder()
                                .model("gpt-4o")
                                .temperature(0.3)
                                .build()
                )).getResult().getOutput().getText();


            }

            String prompt = "Para crear un restaurante, es obligatorio ingresar un usuario y una contraseña.\n" +
                    "Si el usuario no proporciona estos datos, responde directamente con el siguiente mensaje sin agregar introducciones:\n\n" +
                    "\"🚀 Para continuar con la creación de tu restaurante, necesitamos que ingreses un usuario y una contraseña. 🔑 " +
                    "Por favor, proporciona estos datos para seguir adelante. ¡Gracias! 😊\"\n\n" +
                    "Asegúrate de que el mensaje sea claro y amigable, usando emojis.";
            return chatModel.call(new Prompt(
                    prompt,
                    OpenAiChatOptions.builder()
                            .model("gpt-4o")
                            .temperature(0.3)
                            .build()
            )).getResult().getOutput().getText();


        }catch (Exception e){
            return chatModel.call("Explica de manera amigable que hubo un problema al crear el restaurante, sin dar detalles técnicos del error." + e.getMessage());
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

}
