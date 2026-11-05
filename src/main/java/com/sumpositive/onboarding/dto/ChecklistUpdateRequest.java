
package com.sumpositive.onboarding.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChecklistUpdateRequest {

    @NotNull(message = "Completion status is required")
    private Boolean completed;
}

