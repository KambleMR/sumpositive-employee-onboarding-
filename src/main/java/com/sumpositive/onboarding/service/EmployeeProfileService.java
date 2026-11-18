package com.sumpositive.onboarding.service;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sumpositive.onboarding.dto.EmployeeProfileRequest;
import com.sumpositive.onboarding.dto.EmployeeProfileResponse;
import com.sumpositive.onboarding.entity.EmployeeProfile;
import com.sumpositive.onboarding.entity.OnboardingStep;
import com.sumpositive.onboarding.entity.User;
import com.sumpositive.onboarding.enums.OnboardingStepType;
import com.sumpositive.onboarding.repository.EmployeeProfileRepository;
import com.sumpositive.onboarding.repository.OnboardingStepRepository;
import com.sumpositive.onboarding.repository.UserRepository;

@Service
public class EmployeeProfileService {

    private final UserRepository userRepository;
    private final EmployeeProfileRepository employeeRepository;
    private final OnboardingStepRepository stepRepository;
    private final OnboardingCompletionService completionService;

    public EmployeeProfileService(
            EmployeeProfileRepository profileRepository,
            UserRepository userRepository,
            OnboardingStepRepository stepRepository,
            OnboardingCompletionService completionService) {

        this.employeeRepository = profileRepository;
        this.userRepository = userRepository;
        this.stepRepository = stepRepository;
        this.completionService = completionService;
    }

    @Transactional
    public EmployeeProfileResponse saveProfile(Long userId, EmployeeProfileRequest profileRequest) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        EmployeeProfile profile = employeeRepository.findByUserId(userId)
                .orElseGet(EmployeeProfile::new);

        if (profile.getId() == null) {
            profile.setUser(user);
        }

        profile.setFirstName(profileRequest.getFirstName());
        profile.setLastName(profileRequest.getLastName());
        profile.setAddress(profileRequest.getAddress());
        profile.setDateOfBirth(profileRequest.getDateOfBirth());
        profile.setEmergencyContactName(profileRequest.getEmergencyContactName());
        profile.setEmergencyContactNumber(profileRequest.getEmergencyContactNumber());
        profile.setPersonalEmail(profileRequest.getPersonalEmail());
        profile.setPhoneNumber(profileRequest.getPhoneNumber());
        profile.setEmergencyContactRelationship(profileRequest.getEmergencyContactRelationship());

        EmployeeProfile saved = employeeRepository.save(profile);

        OnboardingStep step = stepRepository
                .findByUserIdAndStepType(
                        userId,
                        OnboardingStepType.PROFILE)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Profile onboarding step not found"));

        step.setCompleted(true);
        step.setCompletedAt(LocalDateTime.now());

        stepRepository.save(step);

        // keep User.onboardingCompleted in sync
        completionService.syncCompletion(userId);

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public EmployeeProfileResponse getProfile(Long userId) {

        EmployeeProfile empprofile = employeeRepository.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));

        return mapToResponse(empprofile);
    }

    private EmployeeProfileResponse mapToResponse(EmployeeProfile empPro) {

        EmployeeProfileResponse empresponse = new EmployeeProfileResponse();

        empresponse.setFirstName(empPro.getFirstName());
        empresponse.setLastName(empPro.getLastName());
        empresponse.setAddress(empPro.getAddress());
        empresponse.setDateOfBirth(empPro.getDateOfBirth());
        empresponse.setEmergencyContactName(empPro.getEmergencyContactName());
        empresponse.setEmergencyContactNumber(empPro.getEmergencyContactNumber());
        empresponse.setEmergencyContactRelationship(empPro.getEmergencyContactRelationship());
        empresponse.setId(empPro.getId());
        empresponse.setUserId(empPro.getUser().getId());
        empresponse.setPersonalEmail(empPro.getPersonalEmail());
        empresponse.setPhoneNumber(empPro.getPhoneNumber());

        return empresponse;
    }
}