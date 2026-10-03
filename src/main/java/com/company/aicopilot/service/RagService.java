package com.company.aicopilot.service;

import com.company.aicopilot.model.RagResponse;
import org.springframework.ai.chat.client.ChatClient;
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

    public RagService(
            VectorStore vectorStore,
            ChatClient.Builder chatClientBuilder) {

        this.vectorStore = vectorStore;
        this.chatClient = chatClientBuilder.build();
    }

    public RagResponse answer(String question) {

        SearchRequest searchRequest = SearchRequest.builder()
                .query(question)
                .topK(5)
                .similarityThreshold(0.0)
                .build();

        List<Document> documents =
                vectorStore.similaritySearch(searchRequest);

        String context = documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n---\n\n"));

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