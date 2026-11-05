package com.sumpositive.onboarding.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.sumpositive.onboarding.enums.ChecklistItemType;

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
@Table(name = "UserT")
public class User {

    @Id
    @SequenceGenerator(
        name = "user",
        sequenceName = "SEQ_User",
        initialValue = 1,
        allocationSize = 1
    )
    @GeneratedValue(
        generator = "user",
        strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @Column(unique = true, nullable = false, length = 150)
    private String mail;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private EmployeeProfile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<OnboardingStep> steps;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ChecklistItem> checklistItems;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    @Column(nullable = false)
    private boolean onboardingCompleted = false;
}