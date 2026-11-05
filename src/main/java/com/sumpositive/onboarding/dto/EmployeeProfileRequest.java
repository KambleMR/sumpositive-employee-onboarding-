
package com.sumpositive.onboarding.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor 
@NoArgsConstructor
public class EmployeeProfileRequest {

    @NotBlank(message = "First name is required")
    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

//    @Pattern(
//    	    regexp = "^[+]?[0-9]{7,15}$",
//    	    message = "Enter a valid phone number"
//    	)
    	@Size(max = 15)
    	private String phoneNumber;

    @Email(message = "Enter a valid personal email")
    @Size(max = 100)
    private String personalEmail;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    @Size(max = 255)
    private String address;

    @NotBlank(message = "Emergency contact name is required")
    @Size(max = 100)
    private String emergencyContactName;

    @NotBlank(message = "Emergency contact number is required")
    @Size(max = 15)
    private String emergencyContactNumber;

    @NotBlank(message = "Emergency contact relationship is required")
    @Size(max = 50)
    private String emergencyContactRelationship;
}
