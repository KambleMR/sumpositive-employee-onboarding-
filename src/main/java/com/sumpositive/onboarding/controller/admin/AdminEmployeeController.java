package com.sumpositive.onboarding.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sumpositive.onboarding.dto.admin.AdminEmployeeDetailResponse;
import com.sumpositive.onboarding.dto.admin.AdminEmployeeResponse;
import com.sumpositive.onboarding.service.admin.AdminEmployeeService;

@RestController
@RequestMapping("/api/admin/employees")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEmployeeController {

    private final AdminEmployeeService adminEmployeeService;

    public AdminEmployeeController(
            AdminEmployeeService adminEmployeeService) {
        this.adminEmployeeService = adminEmployeeService;
    }

    @GetMapping
    public ResponseEntity<List<AdminEmployeeResponse>> getAllEmployees() {

        List<AdminEmployeeResponse> employees =
                adminEmployeeService.getAllEmployees();

        return ResponseEntity.ok(employees);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminEmployeeDetailResponse> getEmployee(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                adminEmployeeService.getEmployeeDetail(id));
    }
}