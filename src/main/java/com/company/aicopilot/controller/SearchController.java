package com.company.aicopilot.controller;

import com.company.aicopilot.service.SemanticSearchService;
import org.springframework.ai.document.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SemanticSearchService semanticSearchService;

    public SearchController(SemanticSearchService semanticSearchService) {
        this.semanticSearchService = semanticSearchService;
    }

    @PostMapping
    public List<Map<String, Object>> search(
            @RequestBody Map<String, String> request) {

        String query = request.get("query");

        List<Document> documents =
                semanticSearchService.search(query);

        return documents.stream()
                .map(document -> Map.of(
                        "content", document.getText(),
                        "metadata", document.getMetadata(),
                        "score", document.getScore() != null
                                ? document.getScore()
                                : 0.0
                ))
                .toList();
    }
}