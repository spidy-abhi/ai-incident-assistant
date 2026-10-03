package com.company.aicopilot.controller;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/embedding")
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;

    public EmbeddingController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @PostMapping
    public Map<String, Object> generateEmbedding(@RequestBody Map<String, String> request) {

        String text = request.get("text");

        EmbeddingResponse response =
                embeddingModel.embedForResponse(List.of(text));

        float[] vector = response.getResults()
                .get(0)
                .getOutput();

        return Map.of(
                "text", text,
                "dimensions", vector.length,
                "embedding", vector
        );
    }
}