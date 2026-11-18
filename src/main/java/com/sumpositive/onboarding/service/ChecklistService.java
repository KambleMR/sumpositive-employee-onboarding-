package com.sumpositive.onboarding.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sumpositive.onboarding.dto.ChecklistResponse;
import com.sumpositive.onboarding.dto.ChecklistUpdateRequest;
import com.sumpositive.onboarding.entity.ChecklistItem;
import com.sumpositive.onboarding.enums.OnboardingStepType;
import com.sumpositive.onboarding.repository.ChecklistItemRepository;
import com.sumpositive.onboarding.repository.OnboardingStepRepository;

@Service
public class ChecklistService {

    private final ChecklistItemRepository checklistRepository;
    private final OnboardingStepRepository stepRepository;
    private final OnboardingCompletionService completionService;

    public ChecklistService(
            ChecklistItemRepository checklistRepository,
            OnboardingStepRepository stepRepository,
            OnboardingCompletionService completionService) {
        this.checklistRepository = checklistRepository;
        this.stepRepository = stepRepository;
        this.completionService = completionService;
    }

    @Transactional(readOnly = true)
    public List<ChecklistResponse> getChecklist(Long userId) {

        List<ChecklistItem> items =
                checklistRepository.findByUserId(userId);

        return items.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public ChecklistResponse updateChecklist(
            Long userId,
            Long itemId,
            ChecklistUpdateRequest request) {

        ChecklistItem item = checklistRepository
                .findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Checklist item not found"));

        item.setCompleted(request.getCompleted());

        if (request.getCompleted()) {
            item.setCompletedAt(LocalDateTime.now());
        } else {
            item.setCompletedAt(null);
        }

        ChecklistItem saved =
                checklistRepository.save(item);

        // NEW: keep the JOINING_CHECKLIST onboarding step in sync
        syncJoiningChecklistStep(userId);

        // keep User.onboardingCompleted in sync
        completionService.syncCompletion(userId);

        return mapToResponse(saved);
    }

    private void syncJoiningChecklistStep(Long userId) {

        List<ChecklistItem> items =
                checklistRepository.findByUserId(userId);

        boolean allDone = !items.isEmpty()
                && items.stream().allMatch(ChecklistItem::isCompleted);

        stepRepository
                .findByUserIdAndStepType(
                        userId,
                        OnboardingStepType.JOINING_CHECKLIST)
                .ifPresent(step -> {
                    if (allDone && !step.isCompleted()) {
                        step.setCompleted(true);
                        step.setCompletedAt(LocalDateTime.now());
                        stepRepository.save(step);
                    } else if (!allDone && step.isCompleted()) {
                        step.setCompleted(false);
                        step.setCompletedAt(null);
                        stepRepository.save(step);
                    }
                });
    }

    private ChecklistResponse mapToResponse(
            ChecklistItem item) {

        ChecklistResponse response =
                new ChecklistResponse();

        response.setId(item.getId());
        response.setItemType(item.getItemType());
        response.setCompleted(item.isCompleted());
        response.setCompletedAt(item.getCompletedAt());

        return response;
    }
}