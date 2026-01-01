package io.github.bardiakz.authservice;

import io.github.bardiakz.authservice.event.EventPublisher;
import io.github.bardiakz.authservice.event.UserRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EventPublisher eventPublisher;
    private final UserServiceClient userServiceClient;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            EventPublisher eventPublisher,
            UserServiceClient userServiceClient) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
        this.userServiceClient = userServiceClient;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            
            // Fetch role from user-service
            String role = userServiceClient.getUserRole(request.username());
            
            String jwt = jwtService.generateToken(userDetails, role);

            log.info("User {} logged in successfully with role {}", request.username(), role);
            return ResponseEntity.ok(new LoginResponse(jwt, request.username()));

        } catch (AuthenticationException e) {
            log.warn("Failed login attempt for username: {}", request.username());
            return ResponseEntity.status(401)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request) {

        // Validate default role
        String defaultRole = request.defaultRole() != null ? request.defaultRole() : "STUDENT";
        if (!isValidRole(defaultRole)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid role. Must be STUDENT, INSTRUCTOR, or FACULTY"));
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", "Username is already taken"));
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", "Email is already registered"));
        }

        // Validate password strength
        if (!isPasswordStrong(request.password())) {
            return ResponseEntity.status(400)
                    .body(Map.of("error", "Password must be at least 8 characters"));
        }

        // Create auth user
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        try {
            userRepository.save(user);
            log.info("New user registered: {}", user.getUsername());

            // Publish event for User Service to create profile
            UserRegisteredEvent event = new UserRegisteredEvent(
                    user.getUsername(),
                    user.getEmail(),
                    defaultRole,
                    request.fullName()
            );
            eventPublisher.publishUserRegistered(event);

            return ResponseEntity.status(201)
                    .body(Map.of(
                            "message", "User registered successfully",
                            "username", user.getUsername()
                    ));
        } catch (Exception e) {
            log.error("Error registering user", e);
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Registration failed"));
        }
    }

    private boolean isPasswordStrong(String password) {
        return password != null && password.length() >= 8;
    }

    private boolean isValidRole(String role) {
        return role != null &&
                (role.equals("STUDENT") || role.equals("INSTRUCTOR") || role.equals("FACULTY"));
    }
}