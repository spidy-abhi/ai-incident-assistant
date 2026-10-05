package com.company.aicopilot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/status")
    public Map<String, String> adminStatus() {

        return Map.of(
                "message", "Admin access granted",
                "role", "ADMIN"
        );
    }
}