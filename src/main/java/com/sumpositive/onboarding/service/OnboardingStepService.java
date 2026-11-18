package com.sumpositive.onboarding.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sumpositive.onboarding.dto.OnboardingStepResponse;
import com.sumpositive.onboarding.entity.OnboardingStep;
import com.sumpositive.onboarding.repository.OnboardingStepRepository;

@Service
public class OnboardingStepService {

    private final OnboardingStepRepository stepRepository;
    private final OnboardingCompletionService completionService;

    public OnboardingStepService(
            OnboardingStepRepository stepRepository,
            OnboardingCompletionService completionService) {
        this.stepRepository = stepRepository;
        this.completionService = completionService;
    }

    // GET ALL ONBOARDING STEPS
    @Transactional(readOnly = true)
    public List<OnboardingStepResponse> getAllSteps(Long userId) {

        List<OnboardingStep> steps =
                stepRepository.findByUserId(userId);

        return steps.stream()
                .map(this::mapToResponse)
                .toList();
    }

    // MARK ONBOARDING STEP AS COMPLETED
    @Transactional
    public OnboardingStepResponse completeStep(
            Long userId, Long stepId) {

        OnboardingStep step = stepRepository
                .findByIdAndUserId(stepId, userId)
                .orElseThrow(() ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Onboarding step not found"
                    )
                );

        if (!step.isCompleted()) {
            step.setCompleted(true);
            step.setCompletedAt(LocalDateTime.now());
        }

        OnboardingStep savedStep =
                stepRepository.save(step);

        // keep User.onboardingCompleted in sync
        completionService.syncCompletion(userId);

        return mapToResponse(savedStep);
    }

    // CONVERT ENTITY TO RESPONSE DTO
    private OnboardingStepResponse mapToResponse(
            OnboardingStep step) {

        OnboardingStepResponse response =
                new OnboardingStepResponse();

        response.setId(step.getId());
        response.setStepType(step.getStepType());
        response.setCompleted(step.isCompleted());
        response.setCompletedAt(step.getCompletedAt());

        return response;
    }
}