package com.company.aicopilot.service;

import com.company.aicopilot.model.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    public ChatService(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory) {

        this.chatClient = chatClientBuilder.build();
        this.chatMemory = chatMemory;
    }

    public ChatResponse chat(String question) {

        String conversationId = "default-user";

        // 1. Store the user's message
        chatMemory.add(
                conversationId,
                new UserMessage(question)
        );

        // 2. Retrieve conversation history
        List<Message> conversationHistory =
                chatMemory.get(conversationId);

        // 3. Send the conversation history to the LLM
        String answer = chatClient
                .prompt()
                .messages(conversationHistory)
                .call()
                .content();

        // 4. Store the assistant's response
        chatMemory.add(
                conversationId,
                new AssistantMessage(answer)
        );

        return new ChatResponse(answer);
    }
}