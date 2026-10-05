package com.company.aicopilot.service;

import com.company.aicopilot.model.RagResponse;
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
public class RagService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final QueryRewriteService queryRewriteService;
    private final ChatMemory chatMemory;

    public RagService(
            VectorStore vectorStore,
            ChatClient.Builder chatClientBuilder,
            QueryRewriteService queryRewriteService,
            ChatMemory chatMemory) {

        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
        this.queryRewriteService = queryRewriteService;
        this.chatMemory = chatMemory;
    }

    public RagResponse answer(String question) {

        String conversationId = "default-user";

        // 1. Retrieve previous conversation history
        List<Message> previousMessages =
                chatMemory.get(conversationId);

        String conversationHistory = previousMessages.stream()
                .map(Message::getText)
                .collect(Collectors.joining("\n"));

        // 2. Rewrite the user's question using previous context
        String rewrittenQuestion =
                queryRewriteService.rewrite(
                        question,
                        conversationHistory
                );

        // 3. Store the user's message
        chatMemory.add(
                conversationId,
                new UserMessage(question)
        );

        // 4. Search PGVector using the rewritten question
        SearchRequest searchRequest = SearchRequest.builder()
                .query(rewrittenQuestion)
                .topK(5)
                .similarityThreshold(0.0)
                .build();

        List<Document> documents =
                vectorStore.similaritySearch(searchRequest);

        // 5. Build context from retrieved documents
        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

        // 6. Generate answer using the retrieved context
        String prompt = """
                You are an enterprise incident assistant.

                Answer the user's question using the provided incident context.

                IMPORTANT RULES:

                1. Use the provided context as the source of truth.
                2. If the context contains possible causes, explain them as possible causes.
                3. Do NOT claim that a possible cause is a confirmed root cause.
                4. If the context provides troubleshooting steps, explain them.
                5. Do not invent information that is not present in the context.
                6. Only say that the knowledge base lacks information if the context genuinely
                   does not contain information relevant to the question.

                Incident Context:
                %s

                User Question:
                %s
                """.formatted(context, question);

        String answer = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();

        // 7. Store assistant response in conversation memory
        chatMemory.add(
                conversationId,
                new AssistantMessage(answer)
        );

        // 8. Return source information
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