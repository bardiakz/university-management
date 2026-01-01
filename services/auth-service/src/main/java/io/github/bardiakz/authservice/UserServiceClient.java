package io.github.bardiakz.authservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceClient {
    private static final Logger log = LoggerFactory.getLogger(UserServiceClient.class);
    private final RestTemplate restTemplate;
    private final String userServiceUrl;

    public UserServiceClient(RestTemplate restTemplate, @Value("${user.service.url:http://user-service:8082}") String userServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
    }

    public String getUserRole(String username) {
        try {
            String url = userServiceUrl + "/api/profiles/internal/username/" + username;
            UserProfileResponse response = restTemplate.getForObject(url, UserProfileResponse.class);
            if (response != null && response.getRole() != null) {
                return response.getRole();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch role for user: {}, error: {}", username, e.getMessage());
        }
        return "STUDENT"; // Default role if fetch fails
    }
}
