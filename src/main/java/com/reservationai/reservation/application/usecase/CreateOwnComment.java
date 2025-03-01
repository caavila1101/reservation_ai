package com.reservationai.reservation.application.usecase;

import com.reservationai.reservation.domain.RestaurantComments;
import com.reservationai.reservation.domain.ports.AIService;
import com.reservationai.reservation.domain.ports.RetrieveRestaurantCommentsDomain;
import com.reservationai.reservation.domain.ports.RetrieveRestaurantDomain;
import com.reservationai.reservation.infrastructure.api.dto.RestaurantCommentsDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateOwnComment {
    private final AIService aiService;
    private final RetrieveRestaurantDomain retrieveRestaurantDomain;
    private final RetrieveRestaurantCommentsDomain retrieveRestaurantCommentsDomain;

    public CreateOwnComment(AIService aiService, RetrieveRestaurantDomain retrieveRestaurantDomain, RetrieveRestaurantCommentsDomain retrieveRestaurantCommentsDomain) {
        this.aiService = aiService;
        this.retrieveRestaurantDomain = retrieveRestaurantDomain;
        this.retrieveRestaurantCommentsDomain = retrieveRestaurantCommentsDomain;
    }

    public String execute(RestaurantCommentsDTO restaurantCommentsDTO){

        if(restaurantCommentsDTO.getNameRestaurant().isEmpty()){
            String prompt = "Informa al usuario que el nombre del restaurante es obligatorio para continuar.";
            return aiService.createAnswer(prompt);
        }

        if(restaurantCommentsDTO.getCityRestaurant().isEmpty()){
            String prompt = "Informa al usuario que la ciudad donde se encuentra el restaurante es obligatorio para continuar.";
            return aiService.createAnswer(prompt);
        }

        if(restaurantCommentsDTO.getComments().isEmpty()){
            String prompt = "Informa al usuario que el comentario es obligatorio para continuar.";
            return aiService.createAnswer(prompt);
        }

        if(restaurantCommentsDTO.getRating().isEmpty()){
            String prompt = "Informa al usuario que el rating del restaurante es obligatorio y debe estar entre 1 y 5";

        }

        int ratingValue = Integer.parseInt(restaurantCommentsDTO.getRating());
        if (!(ratingValue >= 1 && ratingValue <= 5)) {
            String prompt = "Informa al usuario que el rating debe estar entre 1 y 5";
            return aiService.createAnswer(prompt);
        }

        Boolean existRestaurantAndCity = existRestaurantAndCity(restaurantCommentsDTO.getNameRestaurant(), restaurantCommentsDTO.getCityRestaurant());

        if(existRestaurantAndCity){
            String prompt = "Eres un asistente de atención al cliente y tu única responsabilidad es analizar un mensaje para determinar si contiene lenguaje ofensivo, grosero o insultante.\n" +
                    "- Si el mensaje NO contiene lenguaje ofensivo, grosero o insultante, devuelve únicamente: True\n" +
                    "- Si el mensaje SÍ contiene lenguaje ofensivo, grosero o insultante, devuelve únicamente: False\n\n" +
                    "No devuelvas ninguna otra palabra, explicación o comentario adicional. Solo responde con `True` o `False`. Este es el comentario a evaluar: " +
                    restaurantCommentsDTO.getComments();
            String extractMessage = aiService.createAnswer(prompt);

            if(extractMessage.contains("True")){
                RestaurantComments restaurantComments = RestaurantComments.builder()
                        .nameRestaurant(restaurantCommentsDTO.getNameRestaurant())
                        .cityRestaurant(restaurantCommentsDTO.getCityRestaurant())
                        .comments(restaurantCommentsDTO.getComments())
                        .rating(restaurantCommentsDTO.getRating())
                        .build();

                List<RestaurantComments> createComment = registerComment(restaurantComments);

                if(!createComment.isEmpty()){
                    String promptSuccessful = "Genera un mensaje breve y amigable usando emojis agradeciendo al usuario por su comentario hecho, informándole que ha sido guardado y que su opinión ayuda a los emprendedores a mejorar el restaurante, no digas mas.";
                    return aiService.createAnswer(promptSuccessful);
                }

                String promptFailed = "Informa al usuario que ocurrió un error interno y pídele que intente nuevamente.";
                return aiService.createAnswer(promptFailed);

            }
            String promptContentRude = "Informa amigablemente usando emojis al usuario que su comentario contiene lenguaje grosero y pídele que lo intente nuevamente sin contenido ofensivo, que estos mensajes deben ser para que el emrendedor siga mejorando su producto";
            return aiService.createAnswer(promptContentRude);


        }

        String promptNotFoundCityAndRestaurant = "Informa amigable y usando emojis al usuario que no encontramos el restaurante en la ciudad indicada y pídele que verifique los datos ingresados.";
        return aiService.createAnswer(promptNotFoundCityAndRestaurant);

    }

    private Boolean existRestaurantAndCity(String name, String city){
        return retrieveRestaurantDomain.findRestaurantByNameAndCity(name, city);
    }

    private List<RestaurantComments> registerComment(RestaurantComments restaurantComments){
        return retrieveRestaurantCommentsDomain.saveComment(restaurantComments);
    }

}
