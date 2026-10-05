package com.company.aicopilot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record IncidentAnalysis(

        @JsonProperty(required = true)
        String summary,

        @JsonProperty(required = true)
        Severity severity,

        @JsonProperty(required = true)
        IncidentStatus status,

        @JsonProperty(required = true)
        String service,

        @JsonProperty(required = true)
        String environment,

        @JsonProperty(required = true)
        List<String> possibleCauses,

        @JsonProperty(required = true)
        List<String> recommendedActions,

        @JsonProperty(required = true)
        Confidence confidence
) {

    public enum Severity {
        CRITICAL,
        HIGH,
        MEDIUM,
        LOW,
        UNKNOWN
    }

    public enum IncidentStatus {
        INVESTIGATING,
        RESOLVED,
        MONITORING,
        UNKNOWN
    }

    public enum Confidence {
        HIGH,
        MEDIUM,
        LOW
    }
}