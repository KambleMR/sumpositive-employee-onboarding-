package com.sumpositive.onboarding.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sumpositive.onboarding.entity.User;
import com.sumpositive.onboarding.enums.Role;
import com.sumpositive.onboarding.repository.UserRepository;
import com.sumpositive.onboarding.service.OnboardingCompletionService;

/**
 * Runs once at every startup and fixes the onboardingCompleted flag
 * for employees who finished their steps before the sync existed.
 * Safe to run repeatedly: it only updates rows that are wrong.
 */
@Configuration
public class OnboardingCompletionBackfill {

    @Bean
    public CommandLineRunner backfillOnboardingCompletion(
            UserRepository userRepository,
            OnboardingCompletionService completionService) {

        return args -> {
            for (User user : userRepository.findAll()) {
                if (user.getRole() == Role.EMPLOYEE) {
                    completionService.syncCompletion(user.getId());
                }
            }
        };
    }
}