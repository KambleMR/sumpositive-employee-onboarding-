
package com.sumpositive.onboarding.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sumpositive.onboarding.dto.OnboardingStepResponse;
import com.sumpositive.onboarding.service.OnboardingStepService;

@RestController
@RequestMapping("/api/onboarding")
public class OnboardingStepController {

    private final OnboardingStepService stepService;

    public OnboardingStepController(
            OnboardingStepService stepService) {
        this.stepService = stepService;
    }

    // GET ALL STEPS
    @GetMapping("/steps/{userId}")
    public ResponseEntity<List<OnboardingStepResponse>> getAllSteps(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                stepService.getAllSteps(userId));
    }

    // COMPLETE A STEP
    @PutMapping("/step/{userId}/{stepId}")
    public ResponseEntity<OnboardingStepResponse> completeStep(
            @PathVariable Long userId,
            @PathVariable Long stepId) {

        return ResponseEntity.ok(
                stepService.completeStep(userId, stepId));
    }
}
