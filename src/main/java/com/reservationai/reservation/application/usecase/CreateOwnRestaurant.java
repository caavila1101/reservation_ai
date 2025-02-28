package com.reservationai.reservation.application.usecase;

import com.reservationai.reservation.domain.OwnRestaurant;
import com.reservationai.reservation.domain.ports.AIService;
import com.reservationai.reservation.domain.ports.RetrieveOwnerRestaurantDomain;
import com.reservationai.reservation.infrastructure.api.dto.OwnRestaurantDTO;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.util.List;
import java.util.UUID;

@Service
public class CreateOwnRestaurant {

    private final RetrieveOwnerRestaurantDomain retrieveOwnerRestaurantDomain;
    private final AIService aiService;

    public CreateOwnRestaurant(RetrieveOwnerRestaurantDomain retrieveOwnerRestaurantDomain, AIService aiService) {
        this.retrieveOwnerRestaurantDomain = retrieveOwnerRestaurantDomain;
        this.aiService = aiService;
    }

    public String execute(OwnRestaurantDTO ownRestaurantDTO) {

        List<OwnRestaurant> existAlreadyUser = getOwenerByUser(ownRestaurantDTO.getUser());
        List<OwnRestaurant> existAlreadyEmail = getOwenerByEmail(ownRestaurantDTO.getEmail());

        if(!existAlreadyUser.isEmpty()){
            String responsePrompt = "Responde de forma amigable y breve. Si el usuario intenta registrarse con un nombre que ya está en uso, dile que el nombre de usuario '"
                    + ownRestaurantDTO.getUser() + "' ya está ocupado. 🚫 Pídele que elija otro diferente . Usa emojis y un tono calido.";
            return aiService.createAnswer(responsePrompt);
        }

        if(!existAlreadyEmail.isEmpty()){
            String responsePrompt = "Responde de forma amigable y breve. Si el usuario intenta registrarse con un correo que ya está en uso, dile que el correo '"
                    + ownRestaurantDTO.getEmail() + "' ya está asociado a otra cuenta. 🚫 Indícale que no es posible registrar otro usuario con el mismo correo y que debe usar uno diferente. Usa emojis y un tono cordial.";
            return aiService.createAnswer(responsePrompt);
        }

        if(!isValidEmail(ownRestaurantDTO.getEmail())){
            String responsePrompt = "Responde de forma amigable y breve. Si el usuario ingresa un correo inválido, dile que debe ingresar un correo válido 📧 " +
                    "con el formato correcto (por ejemplo, 'usuario@ejemplo.com'). Usa un tono cordial y emojis para que el mensaje sea más amigable.";
            return aiService.createAnswer(responsePrompt);
        }

        if(!isValidPassword(ownRestaurantDTO.getPassword())){
            String responsePrompt = "Responde de forma amigable y breve. Si el usuario intenta crear una contraseña, explícale que debe cumplir con los siguientes requisitos: " +
                    "📌 Al menos 6 caracteres, 🔠 Al menos una letra mayúscula, 🔡 Al menos una letra minúscula y 🔣 Al menos un carácter especial (@$!%*?&_). " +
                    "Usa un tono cordial y emojis para que el mensaje sea más amigable.";
            return aiService.createAnswer(responsePrompt);
        }

        if(!isValidUsername(ownRestaurantDTO.getUser())){
            String responsePrompt = "Responde de forma amigable y breve. Si el usuario intenta crear un nombre de usuario, explícale que debe cumplir con los siguientes requisitos: " +
                    "🆔 Debe tener entre 5 y 10 caracteres, 🔠 Debe comenzar con una letra, 🔢 Puede incluir letras, números y cualquier carácter especial. " +
                    "Usa un tono cordial y emojis para que el mensaje sea más amigable.";
            return aiService.createAnswer(responsePrompt);
        }

        UUID uuid = UUID.randomUUID();
        String uuidOwn = uuid.toString();

        OwnRestaurant ownRestaurant = OwnRestaurant.builder()
                .id(uuidOwn)
                .user(ownRestaurantDTO.getUser())
                .email(ownRestaurantDTO.getEmail())
                .password(DigestUtils.sha256Hex(ownRestaurantDTO.getPassword()))
                .build();

        List<OwnRestaurant> createOwnerRestaurant = createOwner(ownRestaurant);

        if(!createOwnerRestaurant.isEmpty()){
            String promptOwnerCreated = "Responde de forma amigable y breve que el usuario fue creado exitosamente y que ahora está un paso más cerca de registrar su restaurante. 🍽️' Usa emojis y un tono entusiasta.";
            return aiService.createAnswer(promptOwnerCreated);
        }

        String promptOwnerNotCreated= "Responde de forma amigable y breve que no pudimos crear el usuario por un problema interno que intente en un rato";
        return aiService.createAnswer(promptOwnerNotCreated);

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

    private static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[@$!%*?&_])[A-Za-z@$!%*?&_]{6,}$";
        return Pattern.matches(passwordRegex, password);
    }

    public static boolean isValidUsername(String username) {
        String usernameRegex = "^[A-Za-z][A-Za-z0-9\\W]{4,9}$";
        return Pattern.matches(usernameRegex, username);
    }

}
