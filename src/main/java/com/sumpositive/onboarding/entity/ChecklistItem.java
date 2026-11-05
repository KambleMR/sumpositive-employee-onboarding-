package com.sumpositive.onboarding.entity;

import java.time.LocalDateTime;

import com.sumpositive.onboarding.enums.ChecklistItemType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(
    name = "ChecklistItemT",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"user_id", "item_type"}
        )
    }
)
public class ChecklistItem {

    @Id
    @SequenceGenerator(
        name = "checklist",
        sequenceName = "SEQ_Checklist",
        initialValue = 1,
        allocationSize = 1
    )
    @GeneratedValue(
        generator = "checklist",
        strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private ChecklistItemType itemType;

    @Column(nullable = false)
    private boolean completed = false;

    private LocalDateTime completedAt;

}