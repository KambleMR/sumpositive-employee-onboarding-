
package com.sumpositive.onboarding.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class EmployeeProfileResponse {

    private Long id;
    private Long userId;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String personalEmail;

    private LocalDate dateOfBirth;
    private String address;

    private String emergencyContactName;
    private String emergencyContactNumber;
    private String emergencyContactRelationship;
}
