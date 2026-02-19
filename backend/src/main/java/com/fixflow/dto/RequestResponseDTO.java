package com.fixflow.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * What the API returns when a client asks for a maintenance request.
 * Flattens related entities (building name, user names) to avoid
 * deep nesting and lazy-loading issues.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponseDTO {

    private Long id;
    private String title;
    private String description;
    private String category;
    private String priority;
    private String status;
    private Integer floorNumber;
    private String roomNumber;

    // Building info (flattened)
    private Long buildingId;
    private String buildingName;

    // Who reported this (flattened)
    private Long createdById;
    private String createdByName;

    // Who is working on it (nullable)
    private Long assignedToId;
    private String assignedToName;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
}
