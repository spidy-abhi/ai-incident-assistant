package com.company.aicopilot.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

@Component
public class IncidentTools {

    @Tool(description = """
            Look up a specific incident by its incident ID.
            Use this when the user asks about a particular incident,
            including its status, severity, service, environment, or summary.
            """)
    public IncidentDetails getIncident(String incidentId) {

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

    @Tool(description = """
            Check the current health status of an application service.
            Use this when the user asks whether a service is healthy,
            operational, degraded, or unavailable.
            """)
    public ServiceHealth getServiceHealth(String service) {

        if ("payments".equalsIgnoreCase(service)) {

            return new ServiceHealth(
                    "payments",
                    "DEGRADED",
                    "Database connection pool exhaustion is affecting the service"
            );
        }

        if ("orders".equalsIgnoreCase(service)) {

            return new ServiceHealth(
                    "orders",
                    "HEALTHY",
                    "No active issues detected"
            );
        }

        return new ServiceHealth(
                service,
                "UNKNOWN",
                "No service health information available"
        );
    }

    @Tool(description = """
            Retrieve recent incidents for a service.
            Use this when the user asks about recent incidents,
            incident history, or recent problems affecting a service.
            """)
    public RecentIncidents getRecentIncidents(String service) {

        if ("payments".equalsIgnoreCase(service)) {

            return new RecentIncidents(
                    service,
                    2,
                    "INC-8472: Database connection pool exhaustion; " +
                    "INC-8461: Increased database response time"
            );
        }

        if ("orders".equalsIgnoreCase(service)) {

            return new RecentIncidents(
                    service,
                    0,
                    "No recent incidents"
            );
        }

        return new RecentIncidents(
                service,
                0,
                "No incident history available"
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

    public record ServiceHealth(
            String service,
            String status,
            String details
    ) {
    }

    public record RecentIncidents(
            String service,
            int count,
            String incidents
    ) {
    }
}