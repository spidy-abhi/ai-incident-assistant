package com.company.aicopilot.service;

import com.company.aicopilot.model.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public ChatResponse chat(String question) {

        String answer = chatClient
                .prompt()
                .user(question)
                .call()
                .content();

        return new ChatResponse(answer);
    }
}