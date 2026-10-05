package com.company.aicopilot.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class IncidentTools {

    @Tool(description = """
            Look up an incident by its incident ID.
            Use this tool when the user asks for the status,
            severity, service, or other details of a specific incident.
            """)
    public IncidentDetails getIncident(String incidentId) {

        // Demo incident data for the MVP
        if ("INC-8472".equalsIgnoreCase(incidentId)) {

            return new IncidentDetails(
                    "INC-8472",
                    "Investigating",
                    "Critical",
                    "payments",
                    "production",
                    "Database connection pool exhaustion"
            );
        }

        return new IncidentDetails(
                incidentId,
                "Unknown",
                "Unknown",
                "Unknown",
                "Unknown",
                "Incident not found"
        );
    }

    public record IncidentDetails(
            String incidentId,
            String status,
            String severity,
            String service,
            String environment,
            String summary
    ) {
    }
}