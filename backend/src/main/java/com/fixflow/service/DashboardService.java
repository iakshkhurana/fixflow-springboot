package com.fixflow.service;

import com.fixflow.dto.DashboardStatsDTO;
import com.fixflow.repository.MaintenanceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides aggregated statistics for the admin/staff dashboard.
 * All counts are computed via database-level aggregation for performance.
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final MaintenanceRequestRepository requestRepo;

    public DashboardStatsDTO getStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // Status counts for the stat cards
        stats.setTotalOpen(requestRepo.countByStatus("OPEN"));
        stats.setTotalAssigned(requestRepo.countByStatus("ASSIGNED"));
        stats.setTotalInProgress(requestRepo.countByStatus("IN_PROGRESS"));
        stats.setTotalResolved(requestRepo.countByStatus("RESOLVED"));
        stats.setTotalClosed(requestRepo.countByStatus("CLOSED"));

        // Convert Object[] results from JPQL GROUP BY queries into typed DTOs
        // JPQL COUNT() returns Long, but some databases may return Integer — handle both
        List<DashboardStatsDTO.CategoryCount> categoryBreakdown = requestRepo.countByCategory()
                .stream()
                .map(row -> new DashboardStatsDTO.CategoryCount(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .toList();

        List<DashboardStatsDTO.PriorityCount> priorityBreakdown = requestRepo.countOpenByPriority()
                .stream()
                .map(row -> new DashboardStatsDTO.PriorityCount(
                        (String) row[0],
                        ((Number) row[1]).longValue()
                ))
                .toList();

        stats.setCategoryBreakdown(categoryBreakdown);
        stats.setPriorityBreakdown(priorityBreakdown);

        return stats;
    }
}
