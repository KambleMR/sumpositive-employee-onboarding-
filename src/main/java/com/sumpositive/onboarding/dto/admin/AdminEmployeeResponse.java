package com.sumpositive.onboarding.dto.admin;

import java.time.LocalDateTime;
import java.util.List;

import com.sumpositive.onboarding.entity.EmployeeProfile;
import com.sumpositive.onboarding.entity.OnboardingStep;
import com.sumpositive.onboarding.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminEmployeeResponse {

    private Long id;

    private String email;

    // null until the employee fills in their profile
    private String fullName;

    private String role;

    private LocalDateTime createdAt;

    private boolean onboardingCompleted;

    private int completedSteps;

    private int totalSteps;

    private int progressPercentage;

    public static AdminEmployeeResponse fromEntity(User user) {

        // Name comes from the employee profile (if they have created one)
        EmployeeProfile profile = user.getProfile();
        String fullName = null;

        if (profile != null && profile.getFirstName() != null) {
            fullName = profile.getFirstName().trim();
            if (profile.getLastName() != null
                    && !profile.getLastName().isBlank()) {
                fullName += " " + profile.getLastName().trim();
            }
        }

        // Progress is calculated from onboarding steps
        // (User.onboardingCompleted flag is never updated by the app)
        List<OnboardingStep> steps = user.getSteps() == null
                ? List.of()
                : user.getSteps();

        int total = steps.size();
        int completed = (int) steps.stream()
                .filter(OnboardingStep::isCompleted)
                .count();

        int progress = total == 0 ? 0 : (completed * 100) / total;

        return new AdminEmployeeResponse(
            user.getId(),
            user.getMail(),
            fullName,
            user.getRole().name(),
            user.getCreatedAt(),
            total > 0 && completed == total,
            completed,
            total,
            progress
        );
    }
}