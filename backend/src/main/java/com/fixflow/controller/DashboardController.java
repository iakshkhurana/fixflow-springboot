package com.fixflow.controller;

import com.fixflow.dto.DashboardStatsDTO;
import com.fixflow.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Provides aggregated stats for the staff/admin dashboard.
 * Powers the stat cards and charts on the dashboard page.
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/dashboard/stats
     * Returns total counts per status, category breakdown, and priority breakdown.
     * Restricted to STAFF and ADMIN (see SecurityConfig).
     */
    @GetMapping("/stats")
    public ResponseEntity<DashboardStatsDTO> getStats() {
        return ResponseEntity.ok(dashboardService.getStats());
    }
}
