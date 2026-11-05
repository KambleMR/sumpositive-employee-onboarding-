
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
import com.sumpositive.onboarding.repository.ChecklistItemRepository;

@Service
public class ChecklistService {

    private final ChecklistItemRepository checklistRepository;

    public ChecklistService(
            ChecklistItemRepository checklistRepository) {
        this.checklistRepository = checklistRepository;
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

        return mapToResponse(saved);
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
