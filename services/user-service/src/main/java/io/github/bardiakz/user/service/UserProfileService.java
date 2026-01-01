package io.github.bardiakz.user.service;

import io.github.bardiakz.user.dto.UserProfileCreateRequest;
import io.github.bardiakz.user.dto.UserProfileResponse;
import io.github.bardiakz.user.dto.UserProfileUpdateRequest;
import io.github.bardiakz.user.entity.Role;
import io.github.bardiakz.user.entity.UserProfile;
import io.github.bardiakz.user.event.EventPublisher;
import io.github.bardiakz.user.event.UserProfileCreatedEvent;
import io.github.bardiakz.user.event.UserRegisteredEvent;
import io.github.bardiakz.user.repository.UserProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserProfileService {

    private static final Logger log = LoggerFactory.getLogger(UserProfileService.class);

    private final UserProfileRepository repository;
    private final EventPublisher eventPublisher;

    public UserProfileService(UserProfileRepository repository, EventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Create profile from UserRegisteredEvent (event-driven)
     */
    @Transactional
    public void createProfileFromEvent(UserRegisteredEvent event) {
        if (repository.existsByEmail(event.getEmail())) {
            log.warn("Profile already exists for email: {}", event.getEmail());
            return;
        }

        UserProfile profile = new UserProfile();
        profile.setUsername(event.getUsername());
        profile.setEmail(event.getEmail());
        profile.setRole(parseRole(event.getDefaultRole()));
        profile.setFullName(event.getFullName());

        UserProfile saved = repository.save(profile);
        log.info("Profile created for userId: {}", saved.getId());

        // Publish confirmation event
        UserProfileCreatedEvent createdEvent = new UserProfileCreatedEvent(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getRole()
        );
        eventPublisher.publishProfileCreated(createdEvent);
    }

    /**
     * Create profile manually (admin endpoint)
     */
    @Transactional
    public UserProfileResponse createProfile(UserProfileCreateRequest request) {
        if (repository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Profile already exists for this email");
        }

        UserProfile profile = new UserProfile();
        profile.setEmail(request.getEmail());
        profile.setUsername(request.getUsername());
        profile.setRole(request.getRole());
        profile.setFullName(request.getFullName());
        profile.setStudentNumber(request.getStudentNumber());
        profile.setPhoneNumber(request.getPhoneNumber());
        profile.setTenantId(request.getTenantId() != null ? request.getTenantId() : 1L);

        UserProfile saved = repository.save(profile);
        return mapToResponse(saved);
    }

    /**
     * Get profile by username
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getProfileByUsername(String username) {
        UserProfile profile = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found for username: " + username));
        return mapToResponse(profile);
    }

    /**
     * Get profile by ID
     */
    @Transactional(readOnly = true)
    public UserProfileResponse getProfileById(Long id) {
        UserProfile profile = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profile not found with id: " + id));
        return mapToResponse(profile);
    }

    /**
     * Get all profiles
     */
    @Transactional(readOnly = true)
    public List<UserProfileResponse> getAllProfiles() {
        return repository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update profile
     */
    @Transactional
    public UserProfileResponse updateProfile(String username, UserProfileUpdateRequest request) {
        UserProfile profile = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Profile not found for username: " + username));

        if (request.getEmail() != null) {
            profile.setEmail(request.getEmail());
        }
        if (request.getFullName() != null) {
            profile.setFullName(request.getFullName());
        }
        if (request.getStudentNumber() != null) {
            profile.setStudentNumber(request.getStudentNumber());
        }
        if (request.getPhoneNumber() != null) {
            profile.setPhoneNumber(request.getPhoneNumber());
        }

        UserProfile updated = repository.save(profile);
        return mapToResponse(updated);
    }

    /**
     * Update user role (admin only)
     */
    @Transactional
    public void updateUserRole(Long userId, Role role) {
        UserProfile profile = repository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found with id: " + userId));
        profile.setRole(role);
        repository.save(profile);
    }

    private Role parseRole(String roleStr) {
        try {
            return Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid role '{}', defaulting to STUDENT", roleStr);
            return Role.STUDENT;
        }
    }

    private UserProfileResponse mapToResponse(UserProfile profile) {
        UserProfileResponse response = new UserProfileResponse();
        response.setId(profile.getId());
        response.setEmail(profile.getEmail());
        response.setUsername(profile.getUsername());
        response.setRole(profile.getRole());
        response.setFullName(profile.getFullName());
        response.setStudentNumber(profile.getStudentNumber());
        response.setPhoneNumber(profile.getPhoneNumber());
        response.setActive(profile.isActive());
        response.setCreatedAt(profile.getCreatedAt());
        response.setUpdatedAt(profile.getUpdatedAt());
        return response;
    }
}