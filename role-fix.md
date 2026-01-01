# Backend Role Check Issue - Marketplace Service

## Problem
Frontend shows: "Failed to create product: Only FACULTY can create products"

This means:
1. ✅ Frontend IS sending the request (token is valid)
2. ✅ Gateway IS routing to marketplace-service
3. ❌ Backend IS REJECTING due to role check

## Root Cause
The marketplace-service (and other services) are likely checking the `role` claim in the JWT token, but **the auth-service no longer includes role in the JWT**.

## What Happened
When you moved roles to user-service:
- JWT tokens from auth-service only contain: `sub` (username), `email`, `iat`, `exp`
- **No `role` field in JWT anymore**
- Backend services still expect `role` claim in JWT

## Solutions

### Option 1: Add role back to JWT (Recommended for now)

**File**: `services/auth-service/.../JwtService.java`

```java
public String generateToken(User user) {
    // Need to fetch role from user-service here
    // For now, add default role or fetch from UserProfile
    
    return Jwts.builder()
            .subject(user.getUsername())
            .claim("email", user.getEmail())
            .claim("role", user.getDefaultRole()) // ✅ Add this
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
}
```

**But wait**: User doesn't have `defaultRole` field anymore after your refactor!

### Option 2: Gateway adds role from user-service (Better)

Make the API Gateway fetch the user's role from user-service and add it to headers:

**File**: `services/api-gateway/.../JwtAuthenticationFilter.java`

```java
@Override
public GatewayFilter apply(Config config) {
    return (exchange, chain) -> {
        // ... existing JWT validation ...
        
        String username = jwtService.extractUsername(token);
        String role = jwtService.extractRole(token); // This returns null now!
        
        // ✅ If role is missing, fetch from user-service
        if (role == null || role.isEmpty()) {
            try {
                role = fetchRoleFromUserService(username);
            } catch (Exception e) {
                log.warn("Failed to fetch role for {}, defaulting to STUDENT", username);
                role = "STUDENT";
            }
        }
        
        // Add to headers for downstream services
        ServerHttpRequest modifiedRequest = exchange.getRequest().mutate()
                .header("X-User-Id", username)
                .header("X-User-Role", role)
                .build();
                
        return chain.filter(exchange.mutate().request(modifiedRequest).build());
    };
}

private String fetchRoleFromUserService(String username) {
    // Call http://user-service:8082/api/profiles/me or use username lookup
    // For now, return default
    return "STUDENT";
}
```

### Option 3: Auth-Service calls User-Service on login (Cleanest)

When a user logs in, auth-service fetches their current role from user-service:

**File**: `services/auth-service/.../AuthService.java`

```java
public LoginResponse login(LoginRequest request) {
    // ... authentication logic ...
    
    // ✅ Fetch current role from user-service
    String role = userServiceClient.getUserRole(user.getUsername());
    
    // Generate token with current role
    String token = jwtService.generateToken(user, role);
    
    return new LoginResponse(token, user.getUsername());
}
```

## Immediate Quick Fix (Temporary)

**Add defaultRole back to User entity in auth-service:**

```java
@Entity
@Table(name = "users")
public class User {
    // ... existing fields ...
    
    @Column(nullable = false)
    private String defaultRole = "STUDENT"; // ✅ Keep this
    
    // ... rest of code ...
}
```

**And include it in JWT:**

```java
public String generateToken(User user) {
    return Jwts.builder()
            .subject(user.getUsername())
            .claim("email", user.getEmail())
            .claim("role", user.getDefaultRole()) // ✅ Use default role for now
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
}
```

## Test Commands

```bash
# 1. Login and check JWT content
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "testuser3", "password": "testuser3"}' | jq -r '.token')

# 2. Decode JWT (paste token into https://jwt.io)
# Check if 'role' claim exists

# 3. Try creating a product
curl -X POST http://localhost:8080/api/marketplace/products \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Product",
    "description": "Test",
    "price": 10.0,
    "stock": 5,
    "category": "Test"
  }'
```

## Recommended Approach

For your current situation, I recommend **Option 3** (Auth-Service calls User-Service on login) because:

1. ✅ Role data stays in one place (user-service)
2. ✅ JWT contains current role at login time
3. ✅ No gateway complexity
4. ✅ Downstream services work without changes

Would you like me to provide the detailed implementation for Option 3?
