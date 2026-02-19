package com.fixflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Aggregated statistics for the admin/staff dashboard.
 * Powers the stat cards, bar chart, and priority breakdown.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {

    private Long totalOpen;
    private Long totalAssigned;
    private Long totalInProgress;
    private Long totalResolved;
    private Long totalClosed;

    // For the "Requests by Category" bar chart
    private List<CategoryCount> categoryBreakdown;

    // For the "Open by Priority" chart
    private List<PriorityCount> priorityBreakdown;

    @Data
    @AllArgsConstructor
    public static class CategoryCount {
        private String category;
        private Long count;
    }

    @Data
    @AllArgsConstructor
    public static class PriorityCount {
        private String priority;
        private Long count;
    }
}
