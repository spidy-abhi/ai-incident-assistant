package com.company.aicopilot.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class QueryRewriteService {

    private final ChatClient chatClient;

    public QueryRewriteService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String rewrite(String question, String conversationHistory) {

        String prompt = """
                You are an enterprise search query rewriting assistant.

                Your job is to rewrite the user's latest question into a
                standalone question that can be understood without the
                previous conversation.

                Rules:

                1. Preserve the user's original intent.
                2. Use conversation history to resolve references such as:
                   "it", "that", "those", "what about it", etc.
                3. Do not answer the question.
                4. Do not add information that is not present in the conversation.
                5. If the question is already standalone, return it unchanged.
                6. Return ONLY the rewritten question.
                7. Do not use quotation marks.
                8. Do not explain your reasoning.

                Conversation History:
                %s

                Latest User Question:
                %s
                """.formatted(conversationHistory, question);

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content()
                .trim();
    }
}