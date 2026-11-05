package com.sumpositive.onboarding.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sumpositive.onboarding.entity.OnboardingStep;
import com.sumpositive.onboarding.entity.User;
import com.sumpositive.onboarding.enums.OnboardingStepType;

public interface OnboardingStepRepository
        extends JpaRepository<OnboardingStep, Long> {

    List<OnboardingStep> findByUserId(Long userId);

    Optional<OnboardingStep> findByUserAndStepType(
            User user,
            OnboardingStepType stepType);

    Optional<OnboardingStep> findByUserIdAndStepType(
            Long userId,
            OnboardingStepType stepType);
    
    Optional<OnboardingStep> findByIdAndUserId(Long id, Long userId);

    long countByUserIdAndCompletedTrue(Long userId);
}