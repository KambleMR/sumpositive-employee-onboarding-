
package com.sumpositive.onboarding.dto;

import java.util.List;

import lombok.Data;

@Data
public class OnboardingProgressResponse {

    private int totalSteps;

    private int completedSteps;

    private int totalChecklistItems;

    private int completedChecklistItems;

    private double completionPercentage;

    private boolean onboardingCompleted;

    private List<OnboardingStepResponse> steps;

    private List<ChecklistResponse> checklistItems;
}
