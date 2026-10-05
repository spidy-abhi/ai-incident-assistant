package com.company.aicopilot.model;

import java.util.List;
import java.util.Map;

public record IncidentAnalysisResponse(
        IncidentAnalysis analysis,
        List<Map<String, Object>> sources
) {
}