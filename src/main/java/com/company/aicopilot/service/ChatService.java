package com.company.aicopilot.service;

import com.company.aicopilot.model.ChatResponse;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    public ChatResponse chat(String question) {

        String answer = "You asked: " + question;

        return new ChatResponse(answer);
    }
}