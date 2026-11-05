
package com.sumpositive.onboarding.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sumpositive.onboarding.dto.ChecklistResponse;
import com.sumpositive.onboarding.dto.ChecklistUpdateRequest;
import com.sumpositive.onboarding.service.ChecklistService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/checklist")
public class ChecklistController {

    private final ChecklistService checklistService;

    public ChecklistController(ChecklistService checklistService) {
        this.checklistService = checklistService;
    }

    // GET CHECKLIST
    @GetMapping("/get/{userId}")
    public ResponseEntity<List<ChecklistResponse>> getChecklist(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                checklistService.getChecklist(userId));
    }

    // UPDATE CHECKLIST
    @PutMapping("/update/{userId}/{itemId}")
    public ResponseEntity<ChecklistResponse> updateChecklist(
            @PathVariable Long userId,
            @PathVariable Long itemId,
            @Valid @RequestBody ChecklistUpdateRequest request) {

        return ResponseEntity.ok(
                checklistService.updateChecklist(
                        userId, itemId, request));
    }
}
