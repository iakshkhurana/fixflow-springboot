package com.fixflow.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 5, message = "Title must be at least 5 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    // Must match the CHECK constraint: PLUMBING, ELECTRICAL, HVAC, GENERAL, ELEVATOR, STRUCTURAL
    @NotBlank(message = "Category is required")
    private String category;

    // Must match: LOW, MEDIUM, HIGH, CRITICAL
    @NotBlank(message = "Priority is required")
    private String priority;

    @NotNull(message = "Building is required")
    private Long buildingId;

    private Integer floorNumber;
    private String roomNumber;
}
