package com.fixflow.repository;

import com.fixflow.model.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {

    // Staff can filter all requests by status
    List<MaintenanceRequest> findByStatusOrderByCreatedAtDesc(String status);

    // Admin view: all requests in a building
    List<MaintenanceRequest> findByBuildingIdOrderByCreatedAtDesc(Long buildingId);

    // Tenant view: only their own requests
    List<MaintenanceRequest> findByCreatedByIdOrderByCreatedAtDesc(Long userId);

    // Staff view: requests assigned to them
    List<MaintenanceRequest> findByAssignedToIdOrderByCreatedAtDesc(Long staffId);

    // Default listing — newest first
    List<MaintenanceRequest> findAllByOrderByCreatedAtDesc();

    // Dashboard: how many requests are in each status
    Long countByStatus(String status);

    // Filter by building and status
    List<MaintenanceRequest> findByBuildingIdAndStatus(Long buildingId, String status);

    // Dashboard chart: requests grouped by category
    @Query("SELECT r.category, COUNT(r) FROM MaintenanceRequest r GROUP BY r.category")
    List<Object[]> countByCategory();

    // Dashboard chart: open requests grouped by priority (excludes resolved/closed)
    @Query("SELECT r.priority, COUNT(r) FROM MaintenanceRequest r WHERE r.status NOT IN ('RESOLVED', 'CLOSED') GROUP BY r.priority")
    List<Object[]> countOpenByPriority();
}
