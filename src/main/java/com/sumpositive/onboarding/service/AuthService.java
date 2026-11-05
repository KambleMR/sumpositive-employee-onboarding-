
package com.sumpositive.onboarding.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sumpositive.onboarding.dto.AuthResponse;
import com.sumpositive.onboarding.dto.LoginRequest;
import com.sumpositive.onboarding.dto.RegisterRequest;
import com.sumpositive.onboarding.entity.ChecklistItem;
import com.sumpositive.onboarding.entity.OnboardingStep;
import com.sumpositive.onboarding.entity.User;
import com.sumpositive.onboarding.enums.ChecklistItemType;
import com.sumpositive.onboarding.enums.OnboardingStepType;
import com.sumpositive.onboarding.repository.ChecklistItemRepository;
import com.sumpositive.onboarding.repository.OnboardingStepRepository;
import com.sumpositive.onboarding.repository.UserRepository;
import com.sumpositive.onboarding.security.JwtService;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final OnboardingStepRepository stepRepository;
    private final ChecklistItemRepository checklistRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            OnboardingStepRepository stepRepository,
            ChecklistItemRepository checklistRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService) {

        this.userRepository = userRepository;
        this.stepRepository = stepRepository;
        this.checklistRepository = checklistRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // EMPLOYEE REGISTRATION
    @Transactional
    public AuthResponse register(RegisterRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        // Check duplicate email
        if (userRepository.existsByMail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists"
            );
        }

        // Check password confirmation
        if (!request.getPassword().equals(
                request.getConfirmPassword())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password and confirm password do not match"
            );
        }

        // Create user
        User user = new User();

        user.setMail(email);

        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        user.setOnboardingCompleted(false);

        // Save user
        User savedUser = userRepository.save(user);

       
        List<OnboardingStep> steps = new ArrayList<>();

        for (OnboardingStepType type :
                OnboardingStepType.values()) {

            OnboardingStep step = new OnboardingStep();

            step.setUser(savedUser);
            step.setStepType(type);
            step.setCompleted(false);

            steps.add(step);
        }

        stepRepository.saveAll(steps);

        // Create all three checklist items
        List<ChecklistItem> checklistItems = new ArrayList<>();

        for (ChecklistItemType type :
                ChecklistItemType.values()) {

            ChecklistItem item = new ChecklistItem();

            item.setUser(savedUser);
            item.setItemType(type);
            item.setCompleted(false);

            checklistItems.add(item);
        }

        checklistRepository.saveAll(checklistItems);

        // Generate JWT
        String token = jwtService.generateToken(email);

        return new AuthResponse(
                token,
                "Bearer",
                savedUser.getId(),
                savedUser.getMail(),
                jwtService.getExpirationMs()
        );
    }

    // EMPLOYEE LOGIN
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {

        String email = request.getEmail()
                .trim()
                .toLowerCase();

        // Find employee
        User user = userRepository.findByMail(email)
                .orElseThrow(() ->
                    new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED,
                            "Invalid email or password"
                    )
                );

        // Verify password
        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid email or password"
            );
        }

        // Generate JWT
        String token = jwtService.generateToken(email);

        return new AuthResponse(
                token,
                "Bearer",
                user.getId(),
                user.getMail(),
                jwtService.getExpirationMs()
        );
    }
}
