package com.company.aicopilot.controller;

import com.company.aicopilot.service.QueryRewriteService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/rewrite")
public class QueryRewriteController {

    private final QueryRewriteService queryRewriteService;

    public QueryRewriteController(QueryRewriteService queryRewriteService) {
        this.queryRewriteService = queryRewriteService;
    }

    @PostMapping
    public Map<String, String> rewrite(
            @RequestBody Map<String, String> request) {

        String question = request.get("question");
        String history = request.getOrDefault("history", "");

        String rewrittenQuestion =
                queryRewriteService.rewrite(question, history);

        return Map.of(
                "originalQuestion", question,
                "rewrittenQuestion", rewrittenQuestion
        );
    }
}