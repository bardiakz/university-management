package io.github.bardiakz.api_gateway;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RoleAuthorizationGatewayFilterFactory
        extends AbstractGatewayFilterFactory<RoleAuthorizationGatewayFilterFactory.Config> {

    private static final Logger log = LoggerFactory.getLogger(RoleAuthorizationGatewayFilterFactory.class);

    public RoleAuthorizationGatewayFilterFactory() {
        super(Config.class);
    }

    @NotNull
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            var request = exchange.getRequest();
            String userRole = request.getHeaders().getFirst("X-User-Role");

            if (userRole == null) {
                log.warn("No user role found in request headers for path: {}", request.getPath());
                return onError(exchange, "Access denied: No role information", HttpStatus.FORBIDDEN);
            }

            // Check if user has the required role
            if (!config.getRole().equalsIgnoreCase(userRole)) {
                log.warn("User with role {} attempted to access {} which requires {}",
                        userRole, request.getPath(), config.getRole());
                return onError(exchange, "Access denied: Insufficient permissions", HttpStatus.FORBIDDEN);
            }

            log.debug("User with role {} authorized for path {}", userRole, request.getPath());
            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus status) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().add("Content-Type", "application/json");
        String body = String.format("{\"error\": \"%s\", \"status\": %d}", message, status.value());
        var buffer = exchange.getResponse().bufferFactory().wrap(body.getBytes());
        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    public static class Config {
        private String role;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }
}