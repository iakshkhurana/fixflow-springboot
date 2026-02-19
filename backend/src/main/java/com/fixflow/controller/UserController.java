package com.fixflow.controller;

import com.fixflow.model.User;
import com.fixflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Provides user listing for admin operations (e.g. staff assignment dropdown).
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepo;

    /**
     * GET /api/users/staff
     * Returns all staff and admin users — used to populate the "Assign to Staff" dropdown.
     * Restricted to ADMIN (see SecurityConfig anyRequest().authenticated() catch-all,
     * ADMIN can access this since it falls through to authenticated check).
     */
    @GetMapping("/staff")
    public ResponseEntity<List<UserSummary>> getStaffUsers() {
        List<User> staffUsers = userRepo.findByRole("STAFF");
        List<User> adminUsers = userRepo.findByRole("ADMIN");

        List<UserSummary> result = new java.util.ArrayList<>();
        staffUsers.forEach(u -> result.add(new UserSummary(u.getId(), u.getFullName(), u.getRole())));
        adminUsers.forEach(u -> result.add(new UserSummary(u.getId(), u.getFullName(), u.getRole())));

        return ResponseEntity.ok(result);
    }

    // Lightweight DTO — we don't need full user details, just name and ID for the dropdown
    public record UserSummary(Long id, String fullName, String role) {}
}
