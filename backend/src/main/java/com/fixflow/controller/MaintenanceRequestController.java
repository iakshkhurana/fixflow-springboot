package com.fixflow.controller;

import com.fixflow.dto.AssignDTO;
import com.fixflow.dto.CommentDTO;
import com.fixflow.dto.CreateRequestDTO;
import com.fixflow.dto.RequestResponseDTO;
import com.fixflow.dto.StatusUpdateDTO;
import com.fixflow.exception.ResourceNotFoundException;
import com.fixflow.model.MaintenanceRequest;
import com.fixflow.model.RequestComment;
import com.fixflow.model.User;
import com.fixflow.repository.RequestCommentRepository;
import com.fixflow.repository.UserRepository;
import com.fixflow.service.MaintenanceRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for maintenance requests.
 * All responses use DTOs — entities are never serialised directly.
 */
@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class MaintenanceRequestController {

    private final MaintenanceRequestService requestService;
    private final RequestCommentRepository commentRepo;
    private final UserRepository userRepo;

    // --- Request CRUD ---

    /**
     * GET /api/requests?status=OPEN
     * Staff/Admin: list all requests, optionally filtered by status.
     */
    @GetMapping
    public ResponseEntity<List<RequestResponseDTO>> getAll(
            @RequestParam(required = false) String status) {
        List<MaintenanceRequest> requests;
        if (status != null && !status.isEmpty()) {
            requests = requestService.getRequestsByStatus(status);
        } else {
            requests = requestService.getAllRequests();
        }
        return ResponseEntity.ok(requests.stream().map(this::toDTO).toList());
    }

    /**
     * GET /api/requests/my
     * Tenant: see only their own submitted requests.
     */
    @GetMapping("/my")
    public ResponseEntity<List<RequestResponseDTO>> getMyRequests(Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        return ResponseEntity.ok(
                requestService.getRequestsByUser(userId).stream().map(this::toDTO).toList()
        );
    }

    /**
     * GET /api/requests/{id}
     * Any authenticated user can view a request's full details.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RequestResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(requestService.getById(id)));
    }

    /**
     * POST /api/requests
     * Tenant creates a new request. Status defaults to OPEN.
     */
    @PostMapping
    public ResponseEntity<RequestResponseDTO> create(
            @Valid @RequestBody CreateRequestDTO dto, Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        MaintenanceRequest created = requestService.createRequest(dto, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
    }

    /**
     * PATCH /api/requests/{id}/status
     * Staff/Admin: move request through the workflow (OPEN → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED)
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<RequestResponseDTO> updateStatus(
            @PathVariable Long id, @Valid @RequestBody StatusUpdateDTO dto) {
        MaintenanceRequest updated = requestService.updateStatus(id, dto.getStatus());
        return ResponseEntity.ok(toDTO(updated));
    }

    /**
     * PATCH /api/requests/{id}/assign
     * Admin only: assign a staff member to this request.
     */
    @PatchMapping("/{id}/assign")
    public ResponseEntity<RequestResponseDTO> assignStaff(
            @PathVariable Long id, @Valid @RequestBody AssignDTO dto) {
        MaintenanceRequest updated = requestService.assignStaff(id, dto.getStaffId());
        return ResponseEntity.ok(toDTO(updated));
    }

    // --- Comments ---

    /**
     * POST /api/requests/{id}/comments
     * Any authenticated user can leave a comment (tenants and staff).
     */
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long id, @RequestBody CommentDTO dto, Authentication auth) {
        Long userId = getUserIdFromAuth(auth);
        MaintenanceRequest request = requestService.getById(id);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        RequestComment comment = RequestComment.builder()
                .request(request)
                .user(user)
                .commentText(dto.getText())
                .build();

        comment = commentRepo.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(toCommentDTO(comment));
    }

    /**
     * GET /api/requests/{id}/comments
     * Returns all comments for a request in chronological order.
     */
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentDTO>> getComments(@PathVariable Long id) {
        List<RequestComment> comments = commentRepo.findByRequestIdOrderByCreatedAtAsc(id);
        return ResponseEntity.ok(comments.stream().map(this::toCommentDTO).toList());
    }

    // --- Helper methods ---

    /**
     * Converts a MaintenanceRequest entity to a flat DTO.
     * Avoids lazy-loading issues by explicitly accessing only needed fields.
     */
    private RequestResponseDTO toDTO(MaintenanceRequest r) {
        return RequestResponseDTO.builder()
                .id(r.getId())
                .title(r.getTitle())
                .description(r.getDescription())
                .category(r.getCategory())
                .priority(r.getPriority())
                .status(r.getStatus())
                .floorNumber(r.getFloorNumber())
                .roomNumber(r.getRoomNumber())
                .buildingId(r.getBuilding() != null ? r.getBuilding().getId() : null)
                .buildingName(r.getBuilding() != null ? r.getBuilding().getName() : null)
                .createdById(r.getCreatedBy() != null ? r.getCreatedBy().getId() : null)
                .createdByName(r.getCreatedBy() != null ? r.getCreatedBy().getFullName() : null)
                .assignedToId(r.getAssignedTo() != null ? r.getAssignedTo().getId() : null)
                .assignedToName(r.getAssignedTo() != null ? r.getAssignedTo().getFullName() : null)
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .resolvedAt(r.getResolvedAt())
                .build();
    }

    private CommentDTO toCommentDTO(RequestComment c) {
        return CommentDTO.builder()
                .id(c.getId())
                .text(c.getCommentText())
                .userId(c.getUser() != null ? c.getUser().getId() : null)
                .userName(c.getUser() != null ? c.getUser().getFullName() : null)
                .createdAt(c.getCreatedAt())
                .build();
    }

    /**
     * Extracts the userId from the JWT token.
     * The JwtAuthenticationFilter stores userId as credentials in the auth token.
     */
    private Long getUserIdFromAuth(Authentication auth) {
        return (Long) auth.getCredentials();
    }
}
