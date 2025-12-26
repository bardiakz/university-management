package com.university.marketplace.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.university.marketplace.dto.OrderResponse;
import com.university.marketplace.dto.OrderStatusResponse;
import com.university.marketplace.dto.PlaceOrderRequest;
import com.university.marketplace.security.RequestAuth;
import com.university.marketplace.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/marketplace/orders")
public class OrderController {

    private final OrderService service;
    private final RequestAuth auth;

    public OrderController(OrderService service, RequestAuth auth) {
        this.service = service;
        this.auth = auth;
    }

    // Place order - فقط دانشجو
    @PostMapping
    public OrderResponse place(
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role,
            @Valid @RequestBody PlaceOrderRequest req
    ) {
        try {
            auth.requireUserId(userId);
            auth.requireRole(role, "STUDENT", "ROLE_STUDENT");
            return service.placeOrder(userId, req);
        } catch (RuntimeException ex) {
            String msg = ex.getMessage() == null ? "Error" : ex.getMessage();

            if (msg.startsWith("Missing")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, msg);
            }
            if ("Forbidden".equals(msg) || msg.startsWith("Forbidden")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg);
            }
            // ✅ مهم: خطای همزمانی موجودی
            if (msg.startsWith("Concurrent update")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, msg); // 409
            }

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
        }
    }

    // My orders - هر کاربر فقط سفارش‌های خودش
    @GetMapping("/my")
    public List<OrderResponse> my(
            @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        try {
            auth.requireUserId(userId);
            return service.myOrders(userId);
        } catch (RuntimeException ex) {
            String msg = ex.getMessage() == null ? "Missing user id" : ex.getMessage();
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, msg);
        }
    }

    // Get order by id - مالک یا admin
    @GetMapping("/{id}")
    public OrderResponse get(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        try {
            auth.requireUserId(userId);
            boolean isAdmin = role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROLE_ADMIN"));
            return service.getOrder(id, userId, isAdmin);
        } catch (RuntimeException ex) {
            String msg = ex.getMessage() == null ? "Error" : ex.getMessage();

            if (msg.startsWith("Missing")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, msg);
            }
            if ("Forbidden".equals(msg) || msg.startsWith("Forbidden")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg);
            }

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        }
    }

    // Track order status - مالک یا admin
    @GetMapping("/{id}/status")
    public OrderStatusResponse status(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Id", required = false) String userId,
            @RequestHeader(value = "X-User-Role", required = false) String role
    ) {
        try {
            auth.requireUserId(userId);
            boolean isAdmin = role != null && (role.equalsIgnoreCase("ADMIN") || role.equalsIgnoreCase("ROLE_ADMIN"));
            return new OrderStatusResponse(id, service.getOrderStatus(id, userId, isAdmin));
        } catch (RuntimeException ex) {
            String msg = ex.getMessage() == null ? "Error" : ex.getMessage();

            if (msg.startsWith("Missing")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, msg);
            }
            if ("Forbidden".equals(msg) || msg.startsWith("Forbidden")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, msg);
            }

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, msg);
        }
    }
}
