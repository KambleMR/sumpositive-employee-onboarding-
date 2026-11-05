
package com.sumpositive.onboarding.dto;

import java.time.LocalDateTime;

import com.sumpositive.onboarding.enums.OnboardingStepType;

import lombok.Data;

@Data
public class OnboardingStepResponse {

    private Long id;

    private OnboardingStepType stepType;

    private boolean completed;

    private LocalDateTime completedAt;
}

