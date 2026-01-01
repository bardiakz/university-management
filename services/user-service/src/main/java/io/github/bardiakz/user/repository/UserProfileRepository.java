package io.github.bardiakz.user.repository;

import io.github.bardiakz.user.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    Optional<UserProfile> findByEmail(String email);
    Optional<UserProfile> findByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
}