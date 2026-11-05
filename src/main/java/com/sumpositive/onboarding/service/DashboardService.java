
package com.sumpositive.onboarding.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sumpositive.onboarding.repository.UserRepository;
import com.sumpositive.onboarding.repository.OnboardingStepRepository;
import com.sumpositive.onboarding.repository.ChecklistItemRepository;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final OnboardingStepRepository stepRepository;
    private final ChecklistItemRepository checklistRepository;

    public DashboardService(
            UserRepository userRepository,
            OnboardingStepRepository stepRepository,
            ChecklistItemRepository checklistRepository) {

        this.userRepository = userRepository;
        this.stepRepository = stepRepository;
        this.checklistRepository = checklistRepository;
    }

    @Transactional(readOnly = true)
    public DashboardData getDashboard(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.NOT_FOUND,
                    "User not found");
        }

        long totalSteps =
                stepRepository.findByUserId(userId).size();

        long completedSteps =
                stepRepository.countByUserIdAndCompletedTrue(userId);

        long totalChecklist =
                checklistRepository.findByUserId(userId).size();

        long completedChecklist =
                checklistRepository.countByUserIdAndCompletedTrue(userId);

        int progress = totalSteps == 0
                ? 0
                : (int) ((completedSteps * 100) / totalSteps);

        return new DashboardData(
                userId,
                totalSteps,
                completedSteps,
                totalChecklist,
                completedChecklist,
                progress
        );
    }

    public record DashboardData(
            Long userId,
            long totalSteps,
            long completedSteps,
            long totalChecklist,
            long completedChecklist,
            int progressPercentage
    ) {}
}

