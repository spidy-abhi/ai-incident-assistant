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
    private final PromptSecurityService promptSecurityService;

    public ChatService(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory,
            IncidentTools incidentTools,
            PromptSecurityService promptSecurityService) {

        this.chatClient = chatClientBuilder.build();
        this.chatMemory = chatMemory;
        this.incidentTools = incidentTools;
        this.promptSecurityService = promptSecurityService;
    }

    public ChatResponse chat(String question) {

        if (!promptSecurityService.isSafe(question)) {
            throw new IllegalArgumentException(
                    "Request blocked by prompt security policy"
            );
        }

        String conversationId = "default-user";

        List<Message> conversationHistory =
                chatMemory.get(conversationId);

        String answer = chatClient
                .prompt()
                .messages(conversationHistory)
                .user(question)
                .tools(incidentTools)
                .call()
                .content();

        chatMemory.add(
                conversationId,
                new UserMessage(question)
        );

        chatMemory.add(
                conversationId,
                new AssistantMessage(answer)
        );

        return new ChatResponse(answer);
    }
}