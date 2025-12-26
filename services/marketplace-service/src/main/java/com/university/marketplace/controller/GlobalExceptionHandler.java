package com.university.marketplace.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        String msg = ex.getMessage() == null ? "Error" : ex.getMessage();

        HttpStatus status = HttpStatus.BAD_REQUEST;

        if (msg.startsWith("Missing")) status = HttpStatus.UNAUTHORIZED;
        else if (msg.equals("Forbidden") || msg.startsWith("Forbidden")) status = HttpStatus.FORBIDDEN;
        else if (msg.startsWith("Concurrent update")) status = HttpStatus.CONFLICT; // ✅ 409
        else if (msg.startsWith("Product not found")) status = HttpStatus.NOT_FOUND;

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", msg);

        return ResponseEntity.status(status).body(body);
    }
}
