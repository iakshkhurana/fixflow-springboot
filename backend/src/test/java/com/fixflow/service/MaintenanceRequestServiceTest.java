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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for MaintenanceRequestService.
 * Uses Mockito to isolate service logic from the database layer.
 */
@ExtendWith(MockitoExtension.class)
class MaintenanceRequestServiceTest {

    @Mock
    private MaintenanceRequestRepository requestRepo;

    @Mock
    private BuildingRepository buildingRepo;

    @Mock
    private UserRepository userRepo;

    @InjectMocks
    private MaintenanceRequestService service;

    // --- Helper builders ---

    private User buildTenant(Long id) {
        return User.builder().id(id).email("tenant@test.com").fullName("Test Tenant").role("TENANT").build();
    }

    private User buildStaff(Long id) {
        return User.builder().id(id).email("staff@test.com").fullName("Test Staff").role("STAFF").build();
    }

    private Building buildBuilding(Long id) {
        return Building.builder().id(id).name("Test Building").address("123 Test St").totalFloors(5).build();
    }

    private MaintenanceRequest buildRequest(Long id, String status) {
        return MaintenanceRequest.builder()
                .id(id)
                .title("Test Request")
                .description("Test description")
                .category("HVAC")
                .priority("HIGH")
                .status(status)
                .building(buildBuilding(1L))
                .createdBy(buildTenant(1L))
                .build();
    }

    // --- Tests ---

    @Test
    void shouldCreateRequestWithOpenStatus() {
        // Given
        CreateRequestDTO dto = new CreateRequestDTO();
        dto.setTitle("Broken AC Unit");
        dto.setDescription("AC not working");
        dto.setCategory("HVAC");
        dto.setPriority("HIGH");
        dto.setBuildingId(1L);

        when(userRepo.findById(1L)).thenReturn(Optional.of(buildTenant(1L)));
        when(buildingRepo.findById(1L)).thenReturn(Optional.of(buildBuilding(1L)));
        when(requestRepo.save(any())).thenAnswer(inv -> {
            MaintenanceRequest r = inv.getArgument(0);
            r.setId(1L);
            return r;
        });

        // When
        MaintenanceRequest result = service.createRequest(dto, 1L);

        // Then — service explicitly sets status to OPEN
        assertThat(result.getStatus()).isEqualTo("OPEN");
        assertThat(result.getTitle()).isEqualTo("Broken AC Unit");
        verify(requestRepo).save(any(MaintenanceRequest.class));
    }

    @Test
    void shouldRejectInvalidStatusTransition() {
        // OPEN cannot jump directly to RESOLVED (must go through ASSIGNED → IN_PROGRESS)
        MaintenanceRequest request = buildRequest(1L, "OPEN");
        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));

        assertThatThrownBy(() -> service.updateStatus(1L, "RESOLVED"))
                .isInstanceOf(InvalidStatusException.class)
                .hasMessageContaining("Cannot transition from OPEN to RESOLVED");
    }

    @Test
    void shouldAllowValidStatusTransition() {
        // OPEN → ASSIGNED is a valid transition
        MaintenanceRequest request = buildRequest(1L, "OPEN");
        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MaintenanceRequest result = service.updateStatus(1L, "ASSIGNED");

        assertThat(result.getStatus()).isEqualTo("ASSIGNED");
    }

    @Test
    void shouldSetResolvedAtWhenResolved() {
        // When status transitions to RESOLVED, resolvedAt should be populated
        MaintenanceRequest request = buildRequest(1L, "IN_PROGRESS");
        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));
        when(requestRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MaintenanceRequest result = service.updateStatus(1L, "RESOLVED");

        assertThat(result.getStatus()).isEqualTo("RESOLVED");
        assertThat(result.getResolvedAt()).isNotNull();
        assertThat(result.getResolvedAt()).isBeforeOrEqualTo(LocalDateTime.now());
    }

    @Test
    void shouldThrowWhenRequestNotFound() {
        // Trying to update a non-existent request should throw ResourceNotFoundException
        when(requestRepo.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus(999L, "ASSIGNED"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void shouldAssignStaffAndUpdateStatus() {
        // Assigning staff to an OPEN request should automatically move it to ASSIGNED
        MaintenanceRequest request = buildRequest(1L, "OPEN");
        User staff = buildStaff(2L);

        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));
        when(userRepo.findById(2L)).thenReturn(Optional.of(staff));
        when(requestRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MaintenanceRequest result = service.assignStaff(1L, 2L);

        assertThat(result.getAssignedTo()).isEqualTo(staff);
        assertThat(result.getStatus()).isEqualTo("ASSIGNED");
    }

    @Test
    void shouldRejectAssignmentToTenant() {
        // Business rule: cannot assign a request to a tenant user
        MaintenanceRequest request = buildRequest(1L, "OPEN");
        User tenant = buildTenant(3L); // role = TENANT

        when(requestRepo.findById(1L)).thenReturn(Optional.of(request));
        when(userRepo.findById(3L)).thenReturn(Optional.of(tenant));

        assertThatThrownBy(() -> service.assignStaff(1L, 3L))
                .isInstanceOf(InvalidStatusException.class)
                .hasMessageContaining("STAFF or ADMIN");
    }
}
