package com.reservationai.reservation.application.usecase;

import com.reservationai.reservation.domain.Restaurant;
import com.reservationai.reservation.domain.RestaurantDetail;
import com.reservationai.reservation.domain.ports.RetrieveRestaurantDomain;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetRestaurantByName {

    private final ChatModel chatModel;
    private final RetrieveRestaurantDomain retrieveRestaurantDomain;

    public GetRestaurantByName(ChatModel chatModel, RetrieveRestaurantDomain retrieveRestaurantDomain) {
        this.chatModel = chatModel;
        this.retrieveRestaurantDomain = retrieveRestaurantDomain;
    }

    public String execute(String name) {
        String prompt = getString(name);
        String extractName = chatModel.call(prompt).toLowerCase().trim();

        if(extractName.length() > 1){
            List<RestaurantDetail> restaurantByName = getCategoriesByName(extractName);

            if(!restaurantByName.isEmpty()){
                String responsePrompt = getString(restaurantByName);

                return chatModel.call(new Prompt(
                        responsePrompt,
                        OpenAiChatOptions.builder()
                                .model("gpt-4o")
                                .temperature(0.2)
                                .build()
                )).getResult().getOutput().getText();
            }
        }

        String promptNotFoundName = "Genera un mensaje amigable informando que no encontramos restaurantes con el nombre" + name +
                ", generame una respuesta no tan larga y no saludes.";
        return chatModel.call(
                new Prompt(
                        promptNotFoundName,
                        OpenAiChatOptions.builder()
                                .model("gpt-4o")
                                .temperature(0.2)
                                .build()
                )
        ).getResult().getOutput().getText();

    }

    private static String getString(List<RestaurantDetail> restaurantByName) {
        RestaurantDetail restaurantName = restaurantByName.get(0);
        String nameRestaurant = restaurantName.getName();
        String addressRestaurant = restaurantName.getAddress();
        String descriptionRestaurant = restaurantName.getDescription();
        String urlRestaurant = restaurantName.getUrl();
        String cityRestaurant = restaurantName.getCity();

        String responsePrompt = "Responde de forma amigable, usando únicamente la información dada. Usa esta data que te paso y dile al usuario que encontraste " +
                "el restaurante y devuelvele esta informacion: " + nameRestaurant + addressRestaurant + descriptionRestaurant + urlRestaurant + cityRestaurant;
        return responsePrompt;
    }

    private String getString(String name) {
        List<String> namesRestaurant = getNamesRestaurant();
        String prompt = "Dada esta lista de nombres de restaurantes: " + namesRestaurant + " y esta nombre ingresado:" + name +", encuentra la mejor coincidencia basada en similitud semántica o variaciones menores" +
                "Si hay una coincidencia, responde solo con el nombre exacto del nombre del restaurante encontrado en la lista dada.\n" +
                "Si no hay coincidencias, responde solo con \"0\", sin añadir punto al final";
        return prompt;
    }

    private List<RestaurantDetail> getCategoriesByName(String name){
        return retrieveRestaurantDomain.findRestaurantByName(name);
    }

    private List<String> getNamesRestaurant(){
        return retrieveRestaurantDomain.findAllNamesRestaurants()
                .stream()
                .map(Restaurant::getName)
                .toList();
    }

}
