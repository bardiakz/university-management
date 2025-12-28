package io.github.bardiakz.user.dto;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import lombok.Data;
import io.github.bardiakz.user.entity.UserProfile;  
@Data
public class UserProfileCreateRequest {
    private String email;
    private String username;
    private String role;
    private String fullName;
    private String studentNumber;
    private String phoneNumber;
    private Long tenantId;
}