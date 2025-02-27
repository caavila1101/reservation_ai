package com.reservationai.reservation.application.usecase;

import com.reservationai.reservation.domain.OwnRestaurant;
import com.reservationai.reservation.domain.Restaurant;
import com.reservationai.reservation.domain.ports.RetrieveOwnerRestaurantDomain;
import com.reservationai.reservation.infrastructure.api.dto.OwnRestaurantDTO;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CreateOwnRestaurant {

    private final ChatModel chatModel;
    private final RetrieveOwnerRestaurantDomain retrieveOwnerRestaurantDomain;

    public CreateOwnRestaurant(ChatModel chatModel, RetrieveOwnerRestaurantDomain retrieveOwnerRestaurantDomain) {
        this.chatModel = chatModel;
        this.retrieveOwnerRestaurantDomain = retrieveOwnerRestaurantDomain;
    }

    public String execute(OwnRestaurantDTO ownRestaurantDTO) {

        List<OwnRestaurant> existAlreadyUser = getOwenerByUser(ownRestaurantDTO.getUser());
        List<OwnRestaurant> existAlreadyEmail = getOwenerByEmail(ownRestaurantDTO.getEmail());

        if(!existAlreadyUser.isEmpty()){
            String responsePrompt = "Responde de forma amigable y breve. Si el usuario intenta registrarse con un nombre que ya está en uso, dile que el nombre de usuario '"
                    + ownRestaurantDTO.getUser() + "' ya está ocupado. 🚫 Pídele que elija otro diferente . Usa emojis y un tono calido.";
            return chatModel.call(responsePrompt);
        }

        if(!existAlreadyEmail.isEmpty()){
            String responsePrompt = "Responde de forma amigable y breve. Si el usuario intenta registrarse con un correo que ya está en uso, dile que el correo '"
                    + ownRestaurantDTO.getEmail() + "' ya está asociado a otra cuenta. 🚫 Indícale que no es posible registrar otro usuario con el mismo correo y que debe usar uno diferente. Usa emojis y un tono cordial.";
            return chatModel.call(responsePrompt);
        }

        UUID uuid = UUID.randomUUID();
        String uuidOwn = uuid.toString();

        OwnRestaurant ownRestaurant = OwnRestaurant.builder()
                .id(uuidOwn)
                .user(ownRestaurantDTO.getUser())
                .email(ownRestaurantDTO.getEmail())
                .password(ownRestaurantDTO.getPassword())
                .build();

        List<OwnRestaurant> createOwnerRestaurant = createOwner(ownRestaurant);

        if(!createOwnerRestaurant.isEmpty()){
            String promptOwnerCreated = "Responde de forma amigable y breve que el usuario fue creado exitosamente y que ahora está un paso más cerca de registrar su restaurante. 🍽️' Usa emojis y un tono entusiasta.";
            return chatModel.call(promptOwnerCreated);
        }

        String promptOwnerNotCreated= "Responde de forma amigable y breve que no pudimos crear el usuario por un problema interno que intente en un rato";
        return chatModel.call(promptOwnerNotCreated);

    }

    private List<OwnRestaurant> getOwenerByUser(String user){
        return retrieveOwnerRestaurantDomain.getOwnerByUser(user);
    }

    private List<OwnRestaurant> getOwenerByEmail(String email){
        return retrieveOwnerRestaurantDomain.getOwnerByEmail(email);
    }

    private List<OwnRestaurant> createOwner(OwnRestaurant ownRestaurant){
        return retrieveOwnerRestaurantDomain.createOwner(ownRestaurant);
    }
}
