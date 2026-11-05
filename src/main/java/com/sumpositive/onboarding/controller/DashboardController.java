
package com.sumpositive.onboarding.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sumpositive.onboarding.service.DashboardService;
import com.sumpositive.onboarding.service.DashboardService.DashboardData;


@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<DashboardData> getDashboard(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                dashboardService.getDashboard(userId));
    }
}
