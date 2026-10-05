package com.company.aicopilot.service;

import com.company.aicopilot.model.RagResponse;
import com.company.aicopilot.tools.IncidentTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class IncidentAssistantService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;
    private final VectorStore vectorStore;
    private final IncidentTools incidentTools;

    public IncidentAssistantService(
            ChatClient.Builder chatClientBuilder,
            ChatMemory chatMemory,
            VectorStore vectorStore,
            IncidentTools incidentTools) {

        this.chatClient = chatClientBuilder.build();
        this.chatMemory = chatMemory;
        this.vectorStore = vectorStore;
        this.incidentTools = incidentTools;
    }

    public RagResponse answer(String question) {

        String conversationId = "default-user";

        // 1. Retrieve previous conversation
        List<Message> previousMessages =
                chatMemory.get(conversationId);

        // 2. Search the knowledge base
        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(5)
                .similarityThreshold(0.0)
                .build();

        List<Document> documents =
                vectorStore.similaritySearch(searchRequest);

        // 3. Build RAG context
        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        // 4. Build prompt
        String prompt = """
                You are an enterprise incident assistant.

                You have access to:

                1. Incident knowledge base context.
                2. Incident lookup and service health tools.
                3. Conversation history.

                Use the tools when the user asks for current incident
                information or service health.

                Use the knowledge base when the user asks about causes,
                troubleshooting, prevention, or technical explanations.

                You may use both tools and the knowledge base when necessary.

                IMPORTANT RULES:

                1. Do not invent incident information.
                2. Do not claim a possible cause is a confirmed root cause.
                3. Clearly distinguish possible causes from confirmed facts.
                4. Use tool results for incident status and service health.
                5. Use the knowledge base for technical explanations.
                6. If the available evidence is insufficient, say so.

                Knowledge Base Context:
                %s

                User Question:
                %s
                """.formatted(context, question);

        // 5. Store user message
        chatMemory.add(
                conversationId,
                new UserMessage(question)
        );

        // 6. Ask the LLM with tools available
        String answer = chatClient
                .prompt()
                .messages(previousMessages)
                .user(prompt)
                .tools(incidentTools)
                .call()
                .content();

        // 7. Store assistant response
        chatMemory.add(
                conversationId,
                new AssistantMessage(answer)
        );

        // 8. Return answer and sources
        List<Map<String, Object>> sources = documents.stream()
                .map(document -> Map.<String, Object>of(
                        "source",
                        document.getMetadata()
                                .getOrDefault("source", "unknown"),
                        "score",
                        document.getScore() != null
                                ? document.getScore()
                                : 0.0
                ))
                .toList();

        return new RagResponse(
                question,
                answer,
                sources
        );
    }
}