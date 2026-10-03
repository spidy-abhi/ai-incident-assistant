package com.company.aicopilot.controller;

import com.company.aicopilot.service.DocumentIngestionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentIngestionService documentIngestionService;

    public DocumentController(
            DocumentIngestionService documentIngestionService) {

        this.documentIngestionService = documentIngestionService;
    }

    @PostMapping("/ingest")
    public Map<String, String> ingest() {

        documentIngestionService.ingestDatabaseIncident();

        return Map.of(
                "status", "success",
                "message", "Incident document ingested successfully"
        );
    }
}