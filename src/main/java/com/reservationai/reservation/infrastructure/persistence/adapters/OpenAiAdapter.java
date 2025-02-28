package com.reservationai.reservation.infrastructure.persistence.adapters;

import com.reservationai.reservation.domain.ports.AIService;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;

@Component
public class OpenAiAdapter implements AIService {

    private final ChatModel chatModel;

    public OpenAiAdapter(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @Override
    public String createAnswer(String message) {
        Prompt prompt = new Prompt(
                message,
                OpenAiChatOptions.builder()
                        .model("gpt-4o")
                        .temperature(0.3)
                        .build()
        );

        return chatModel.call(prompt).getResult().getOutput().getText();
    }
}
