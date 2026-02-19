package com.fixflow.service;

import com.fixflow.dto.CreateRequestDTO;
import com.fixflow.exception.InvalidStatusException;
import com.fixflow.exception.ResourceNotFoundException;
import com.fixflow.model.Building;
import com.fixflow.model.MaintenanceRequest;
import com.fixflow.model.User;
import com.fixflow.repository.BuildingRepository;
import com.fixflow.repository.MaintenanceRequestRepository;
import com.fixflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Core business logic for maintenance requests.
 *
 * Key design decision: status transitions are strictly validated using a
 * transition map. This prevents requests from jumping to invalid states
 * (e.g. an OPEN request cannot become RESOLVED without going through ASSIGNED/IN_PROGRESS).
 */
@Service
@RequiredArgsConstructor
public class MaintenanceRequestService {

    private final MaintenanceRequestRepository requestRepo;
    private final BuildingRepository buildingRepo;
    private final UserRepository userRepo;

    /**
     * Defines which status transitions are allowed.
     * CLOSED is a terminal state — nothing can transition out of it.
     */
    private static final Map<String, List<String>> VALID_TRANSITIONS = Map.of(
            "OPEN",        List.of("ASSIGNED"),
            "ASSIGNED",    List.of("IN_PROGRESS", "OPEN"),    // Can un-assign back to OPEN
            "IN_PROGRESS", List.of("RESOLVED", "ASSIGNED"),   // Can re-assign if needed
            "RESOLVED",    List.of("CLOSED", "IN_PROGRESS"),  // Can reopen if issue recurs
            "CLOSED",      List.of()                          // Terminal state
    );

    /**
     * Creates a new maintenance request with OPEN status.
     * Status is set explicitly here (not relying on @PrePersist) so unit tests can verify it.
     */
    public MaintenanceRequest createRequest(CreateRequestDTO dto, Long userId) {
        User tenant = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Building building = buildingRepo.findById(dto.getBuildingId())
                .orElseThrow(() -> new ResourceNotFoundException("Building not found"));

        MaintenanceRequest request = MaintenanceRequest.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .priority(dto.getPriority())
                .floorNumber(dto.getFloorNumber())
                .roomNumber(dto.getRoomNumber())
                .building(building)
                .createdBy(tenant)
                .status("OPEN") // Explicitly set so unit tests don't need @PrePersist
                .build();

        return requestRepo.save(request);
    }

    /**
     * Validates the transition and updates status.
     * Sets resolvedAt timestamp when a request reaches RESOLVED state.
     */
    public MaintenanceRequest updateStatus(Long requestId, String newStatus) {
        MaintenanceRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));

        List<String> allowed = VALID_TRANSITIONS.getOrDefault(request.getStatus(), List.of());
        if (!allowed.contains(newStatus)) {
            throw new InvalidStatusException(
                    "Cannot transition from " + request.getStatus() + " to " + newStatus +
                    ". Allowed: " + allowed
            );
        }

        request.setStatus(newStatus);

        // Track exactly when each issue was resolved for reporting
        if ("RESOLVED".equals(newStatus)) {
            request.setResolvedAt(LocalDateTime.now());
        }

        return requestRepo.save(request);
    }

    /**
     * Assigns a staff member to a request.
     * If the request was OPEN, automatically moves it to ASSIGNED status.
     */
    public MaintenanceRequest assignStaff(Long requestId, Long staffId) {
        MaintenanceRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));
        User staff = userRepo.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found with id: " + staffId));

        // Business rule: can only assign to staff or admin, not tenants
        if (!"STAFF".equals(staff.getRole()) && !"ADMIN".equals(staff.getRole())) {
            throw new InvalidStatusException("Can only assign requests to STAFF or ADMIN users");
        }

        request.setAssignedTo(staff);

        // Auto-advance status from OPEN to ASSIGNED when someone takes ownership
        if ("OPEN".equals(request.getStatus())) {
            request.setStatus("ASSIGNED");
        }

        return requestRepo.save(request);
    }

    public List<MaintenanceRequest> getAllRequests() {
        return requestRepo.findAllByOrderByCreatedAtDesc();
    }

    public List<MaintenanceRequest> getRequestsByUser(Long userId) {
        return requestRepo.findByCreatedByIdOrderByCreatedAtDesc(userId);
    }

    public List<MaintenanceRequest> getRequestsByStatus(String status) {
        return requestRepo.findByStatusOrderByCreatedAtDesc(status);
    }

    public MaintenanceRequest getById(Long id) {
        return requestRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + id));
    }
}
