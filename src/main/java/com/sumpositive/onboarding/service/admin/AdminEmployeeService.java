package com.sumpositive.onboarding.service.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.sumpositive.onboarding.dto.admin.AdminEmployeeDetailResponse;
import com.sumpositive.onboarding.dto.admin.AdminEmployeeResponse;
import com.sumpositive.onboarding.enums.Role;
import com.sumpositive.onboarding.repository.UserRepository;

@Service
public class AdminEmployeeService {

    private final UserRepository userRepository;

    public AdminEmployeeService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminEmployeeResponse> getAllEmployees() {

        return userRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                // Do not include Admin accounts in the employee list
                .filter(user -> user.getRole() != null
                        && user.getRole().name().equals("EMPLOYEE"))
                .map(AdminEmployeeResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminEmployeeDetailResponse getEmployeeDetail(Long id) {

        return userRepository.findById(id)
                // Admin accounts are not employees
                .filter(user -> user.getRole() == Role.EMPLOYEE)
                .map(AdminEmployeeDetailResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Employee not found"));
    }
}