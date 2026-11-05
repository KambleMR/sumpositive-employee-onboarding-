package com.sumpositive.onboarding.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sumpositive.onboarding.entity.ChecklistItem;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {
	List<ChecklistItem> findByUserId(Long userId);

    Optional<ChecklistItem> findByIdAndUserId(
            Long id, Long userId);
    
    long countByUserIdAndCompletedTrue(Long userId);
}
