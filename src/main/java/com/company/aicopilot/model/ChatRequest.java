package com.company.aicopilot.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ChatRequest {

    @NotBlank(message = "Question must not be empty")
    @Size(max = 2000, message = "Question must not exceed 2000 characters")
    private String question;

    public ChatRequest() {
    }

    public ChatRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}