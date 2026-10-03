package com.company.aicopilot.controller;

import com.company.aicopilot.model.RagResponse;
import com.company.aicopilot.service.RagService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rag")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }

    @PostMapping
    public RagResponse ask(
            @RequestBody Map<String, String> request) {

        String question = request.get("question");

        return ragService.answer(question);
    }
}