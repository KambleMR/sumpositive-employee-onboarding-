package com.sumpositive.onboarding.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
@Table(name = "EmployeeProfileT")
public class EmployeeProfile {

    @Id
    @SequenceGenerator(
        name = "profile",
        sequenceName = "SEQ_Profile",
        initialValue = 1,
        allocationSize = 1
    )
    @GeneratedValue(
        generator = "profile",
        strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @OneToOne
    @JoinColumn(
        name = "user_id",
        nullable = false,
        unique = true
    )
    private User user;

    @Column(nullable = false, length = 50)
    private String firstName;

    @Column(length = 50)
    private String lastName;

    @Column(length = 15)
    private String phoneNumber;

    @Column(length = 100)
    private String personalEmail;

    private LocalDate dateOfBirth;

    @Column(length = 255)
    private String address;

    @Column(nullable = false, length = 100)
    private String emergencyContactName;

    @Column(nullable = false, length = 15)
    private String emergencyContactNumber;

    @Column(nullable = false, length = 50)
    private String emergencyContactRelationship;

}