package com.sumpositive.onboarding.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sumpositive.onboarding.repository.OnboardingStepRepository;
import com.sumpositive.onboarding.repository.UserRepository;

/**
 * Keeps User.onboardingCompleted in sync with the onboarding steps.
 * Completed = the user has steps AND every step is completed.
 */
@Service
public class OnboardingCompletionService {

    private final UserRepository userRepository;
    private final OnboardingStepRepository stepRepository;

    public OnboardingCompletionService(
            UserRepository userRepository,
            OnboardingStepRepository stepRepository) {
        this.userRepository = userRepository;
        this.stepRepository = stepRepository;
    }

    @Transactional
    public void syncCompletion(Long userId) {

        long total = stepRepository.findByUserId(userId).size();
        long done = stepRepository.countByUserIdAndCompletedTrue(userId);

        boolean completed = total > 0 && done == total;

        userRepository.findById(userId).ifPresent(user -> {
            if (user.isOnboardingCompleted() != completed) {
                user.setOnboardingCompleted(completed);
                userRepository.save(user);
            }
        });
    }
}