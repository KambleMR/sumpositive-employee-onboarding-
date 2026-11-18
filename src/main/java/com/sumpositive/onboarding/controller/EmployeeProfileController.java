package com.sumpositive.onboarding.controller;

import com.sumpositive.onboarding.dto.EmployeeProfileRequest;
import com.sumpositive.onboarding.dto.EmployeeProfileResponse;
import com.sumpositive.onboarding.service.EmployeeProfileService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@Validated
public class EmployeeProfileController {

    private final EmployeeProfileService empService;

    public EmployeeProfileController(EmployeeProfileService empService) {
        this.empService = empService;
    }

    // SAVE EMPLOYEE PROFILE
    @PostMapping("/save/{userId}")
    public ResponseEntity<EmployeeProfileResponse> saveProfile(
            @PathVariable Long userId,
            @Valid @RequestBody EmployeeProfileRequest empRequest) {

        EmployeeProfileResponse empResponse =
                empService.saveProfile(userId, empRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(empResponse);
    }

    // UPDATE EMPLOYEE PROFILE
    @PutMapping("/update/{userId}")
    public ResponseEntity<EmployeeProfileResponse> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody EmployeeProfileRequest empRequest) {

        EmployeeProfileResponse empResponse =
                empService.saveProfile(userId, empRequest);

        return ResponseEntity.ok(empResponse);
    }
    
    
 

    // GET EMPLOYEE PROFILE
    @GetMapping("/get/{userId}")
    public ResponseEntity<EmployeeProfileResponse> getProfile(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                empService.getProfile(userId)
        );
    }
}
