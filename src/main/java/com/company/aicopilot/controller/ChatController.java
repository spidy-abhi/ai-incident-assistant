package com.company.aicopilot.controller;

import com.company.aicopilot.model.ChatRequest;
import com.company.aicopilot.model.ChatResponse;
import com.company.aicopilot.service.ChatService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(
            @Valid @RequestBody ChatRequest request) {

        return chatService.chat(request.getQuestion());
    }
}