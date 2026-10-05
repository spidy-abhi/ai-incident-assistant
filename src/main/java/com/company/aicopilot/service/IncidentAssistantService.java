package com.company.aicopilot.service;

import com.company.aicopilot.model.IncidentAnalysis;
import com.company.aicopilot.model.IncidentAnalysisResponse;
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

    public IncidentAnalysisResponse answer(String question) {

        String conversationId = "default-user";

        // 1. Retrieve previous conversation history
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

        // 4. Build structured-output prompt
        String prompt = """
                You are an enterprise incident analysis assistant.

                Analyze the user's incident question using the available
                knowledge base and incident tools.

                You have access to:

                1. Incident knowledge base context.
                2. Incident lookup and service health tools.
                3. Conversation history.

                Use tools when the user asks about a specific incident,
                incident status, severity, or service health.

                Use the knowledge base when the user asks about:
                - possible causes
                - troubleshooting
                - prevention
                - technical explanations

                You may use both tools and the knowledge base.

                IMPORTANT RULES:

                1. Do not invent incident information.
                2. Do not claim a possible cause is a confirmed root cause.
                3. Clearly distinguish possible causes from confirmed facts.
                4. Use tool results for incident status and service health.
                5. Use the knowledge base for technical explanations.
                6. If the evidence is insufficient, say so.
                7. The confidence field must be LOW, MEDIUM, or HIGH.

8. When getIncident() returns an incident, its severity is authoritative.
   Copy that severity exactly.

9. When getIncident() returns an incident, its status is authoritative.
   Copy that status exactly.

10. Never infer or downgrade the severity when the incident tool provides
    a known severity.

11. Never replace a known incident status with a generic status such
    as UNRESOLVED.

12. If the incident tool reports:
    severity = Critical
    status = Investigating

    then the structured response MUST contain:
    severity = CRITICAL
    status = INVESTIGATING.
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

        // 6. Ask the LLM and receive a typed IncidentAnalysis object
       IncidentAnalysis analysis = chatClient
        .prompt()
        .messages(previousMessages)
        .user(prompt)
        .tools(incidentTools)
        .call()
        .entity(
                IncidentAnalysis.class,
                spec -> spec
                        .useProviderStructuredOutput()
                        .validateSchema()
        );

        // 7. Store a representation of the structured response
        chatMemory.add(
                conversationId,
                new AssistantMessage(
                        analysis.toString()
                )
        );

        // 8. Build source information
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

        return new IncidentAnalysisResponse(
                analysis,
                sources
        );
    }
}