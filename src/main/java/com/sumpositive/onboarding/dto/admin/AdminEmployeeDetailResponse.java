package com.sumpositive.onboarding.dto.admin;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import com.sumpositive.onboarding.entity.ChecklistItem;
import com.sumpositive.onboarding.entity.EmployeeProfile;
import com.sumpositive.onboarding.entity.OnboardingStep;
import com.sumpositive.onboarding.entity.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminEmployeeDetailResponse {

    private Long id;
    private String email;
    private String fullName;
    private LocalDateTime createdAt;

    private boolean onboardingCompleted;
    private int completedSteps;
    private int totalSteps;
    private int progressPercentage;

    // Profile details (all null until the employee fills in the profile)
    private String phoneNumber;
    private String personalEmail;
    private String emergencyContactName;
    private String emergencyContactNumber;
    private String emergencyContactRelationship;

    private List<StepItem> steps;
    private List<ChecklistEntry> checklist;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StepItem {
        private String stepType;
        private boolean completed;
        private LocalDateTime completedAt;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChecklistEntry {
        private String itemType;
        private boolean completed;
        private LocalDateTime completedAt;
    }

    public static AdminEmployeeDetailResponse fromEntity(User user) {

        // Reuse the summary logic (name + progress calculation)
        AdminEmployeeResponse summary = AdminEmployeeResponse.fromEntity(user);

        AdminEmployeeDetailResponse response = new AdminEmployeeDetailResponse();

        response.setId(summary.getId());
        response.setEmail(summary.getEmail());
        response.setFullName(summary.getFullName());
        response.setCreatedAt(summary.getCreatedAt());
        response.setOnboardingCompleted(summary.isOnboardingCompleted());
        response.setCompletedSteps(summary.getCompletedSteps());
        response.setTotalSteps(summary.getTotalSteps());
        response.setProgressPercentage(summary.getProgressPercentage());

        EmployeeProfile profile = user.getProfile();

        if (profile != null) {
            response.setPhoneNumber(profile.getPhoneNumber());
            response.setPersonalEmail(profile.getPersonalEmail());
            response.setEmergencyContactName(profile.getEmergencyContactName());
            response.setEmergencyContactNumber(profile.getEmergencyContactNumber());
            response.setEmergencyContactRelationship(
                    profile.getEmergencyContactRelationship());
        }

        List<OnboardingStep> userSteps = user.getSteps() == null
                ? List.of()
                : user.getSteps();

        response.setSteps(
            userSteps.stream()
                .sorted(Comparator.comparing(OnboardingStep::getStepType))
                .map(step -> new StepItem(
                        step.getStepType().name(),
                        step.isCompleted(),
                        step.getCompletedAt()))
                .toList()
        );

        List<ChecklistItem> userChecklist = user.getChecklistItems() == null
                ? List.of()
                : user.getChecklistItems();

        response.setChecklist(
            userChecklist.stream()
                .sorted(Comparator.comparing(ChecklistItem::getItemType))
                .map(item -> new ChecklistEntry(
                        item.getItemType().name(),
                        item.isCompleted(),
                        item.getCompletedAt()))
                .toList()
        );

        return response;
    }
}