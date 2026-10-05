package com.company.aicopilot.controller;

import com.company.aicopilot.model.RagResponse;
import com.company.aicopilot.service.IncidentAssistantService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/assistant")
public class IncidentAssistantController {

    private final IncidentAssistantService incidentAssistantService;

    public IncidentAssistantController(
            IncidentAssistantService incidentAssistantService) {

        this.incidentAssistantService = incidentAssistantService;
    }

    @PostMapping
    public RagResponse ask(
            @RequestBody Map<String, String> request) {

        String question = request.get("question");

        return incidentAssistantService.answer(question);
    }
}