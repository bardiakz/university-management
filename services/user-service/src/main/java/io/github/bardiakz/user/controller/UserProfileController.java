package io.github.bardiakz.user.controller;

import io.github.bardiakz.user.dto.UserProfileCreateRequest;
import io.github.bardiakz.user.dto.UserProfileResponse;
import io.github.bardiakz.user.dto.UserProfileUpdateRequest;
import io.github.bardiakz.user.entity.Role;
import io.github.bardiakz.user.entity.UserProfile;
import io.github.bardiakz.user.service.UserProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profiles")
public class UserProfileController {

    private final UserProfileService service;

    public UserProfileController(UserProfileService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> createProfile(@RequestBody UserProfileCreateRequest request) {
        UserProfileResponse response = service.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get the current user's profile
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getMyProfile(@AuthenticationPrincipal String username) {
        try {
            UserProfileResponse profile = service.getProfileByUsername(username);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    /**
     * Update the current user's profile
     */
    @PutMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMyProfile(
            @AuthenticationPrincipal String username,
            @RequestBody UserProfileUpdateRequest request) {
        try {
            UserProfileResponse updated = service.updateProfile(username, request);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    /**
     * Get a specific user profile by ID (admin only)
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable Long id) {
        try {
            UserProfileResponse profile = service.getProfileById(id);
            return ResponseEntity.ok(profile);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(null);
        }
    }

    /**
     * Get all user profiles (admin only)
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserProfileResponse>> getAllProfiles() {
        List<UserProfileResponse> profiles = service.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    /**
     * Update a user's role (admin only)
     */
    @PatchMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            String roleStr = request.get("role");
            Role role = Role.valueOf(roleStr.toUpperCase());
            service.updateUserRole(id, role);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}