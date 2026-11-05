
package com.sumpositive.onboarding.dto;

import java.time.LocalDateTime;

import com.sumpositive.onboarding.enums.ChecklistItemType;

import lombok.Data;

@Data
public class ChecklistResponse {

    private Long id;

    private ChecklistItemType itemType;

    private boolean completed;

    private LocalDateTime completedAt;
}
