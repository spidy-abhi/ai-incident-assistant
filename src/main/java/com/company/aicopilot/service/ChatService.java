package com.company.aicopilot.service;

import com.company.aicopilot.model.ChatResponse;
import com.company.aicopilot.tools.IncidentTools;
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
    private final IncidentTools incidentTools;

    public ChatService(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory,
            IncidentTools incidentTools) {

        this.chatClient = chatClientBuilder.build();
        this.chatMemory = chatMemory;
        this.incidentTools = incidentTools;
    }

    public ChatResponse chat(String question) {

        String conversationId = "default-user";

        // 1. Retrieve previous conversation history
        List<Message> conversationHistory =
                chatMemory.get(conversationId);

        // 2. Send question to the LLM with incident tools available
        String answer = chatClient
                .prompt()
                .messages(conversationHistory)
                .user(question)
                .tools(incidentTools)
                .call()
                .content();

        // 3. Store the user's message
        chatMemory.add(
                conversationId,
                new UserMessage(question)
        );

        // 4. Store the assistant's response
        chatMemory.add(
                conversationId,
                new AssistantMessage(answer)
        );

        return new ChatResponse(answer);
    }
}