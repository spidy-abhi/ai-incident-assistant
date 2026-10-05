package com.company.aicopilot.service;

import org.springframework.stereotype.Service;

@Service
public class PromptSecurityService {

    public boolean isSafe(String question) {

        if (question == null || question.isBlank()) {
            return false;
        }

        String normalized = question.toLowerCase();

        String[] suspiciousPatterns = {
                "ignore previous instructions",
                "ignore all previous instructions",
                "forget previous instructions",
                "system prompt",
                "reveal your instructions",
                "show me your prompt",
                "developer message",
                "jailbreak",
                "bypass your rules"
        };

        for (String pattern : suspiciousPatterns) {
            if (normalized.contains(pattern)) {
                return false;
            }
        }

        return true;
    }
}