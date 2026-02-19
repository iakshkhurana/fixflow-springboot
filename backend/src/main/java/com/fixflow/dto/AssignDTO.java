package com.fixflow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AssignDTO {

    @NotNull(message = "Staff ID is required")
    private Long staffId;
}
