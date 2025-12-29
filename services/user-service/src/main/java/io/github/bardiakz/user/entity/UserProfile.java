package io.github.bardiakz.user.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_profiles")
@Data
public class UserProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;               

    @Column(unique = true, nullable = false)
    private String email;         

    private String username;       
    private String role;

    private String fullName;
    private String studentNumber;
    private String phoneNumber;
    private Long tenantId;
    private boolean active = true;
}