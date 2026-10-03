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
        String service = request.get("service");
        String environment = request.get("environment");

        List<Document> documents;

        if (service != null && environment != null) {

            documents = semanticSearchService.search(
                    query,
                    service,
                    environment
            );

        } else {

            documents = semanticSearchService.search(query);
        }

        return documents.stream()
                .map(document -> Map.<String, Object>of(
                        "content", document.getText(),
                        "metadata", document.getMetadata(),
                        "score", document.getScore() != null
                                ? document.getScore()
                                : 0.0
                ))
                .toList();
    }
}