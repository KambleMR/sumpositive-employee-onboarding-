package com.sumpositive.onboarding.entity;

import java.time.LocalDateTime;

import com.sumpositive.onboarding.enums.ChecklistItemType;
import com.sumpositive.onboarding.enums.OnboardingStepType;

import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(
    name = "OnboardingStepT",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {"user_id", "step_type"}
        )
    }
)
public class OnboardingStep {

    @Id
    @SequenceGenerator(
        name = "step",
        sequenceName = "SEQ_Step",
        initialValue = 1,
        allocationSize = 1
    )
    @GeneratedValue(
        generator = "step",
        strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "step_type", nullable = false)
    private OnboardingStepType stepType;

    @Column(nullable = false)
    private boolean completed = false;

    private LocalDateTime completedAt;

}