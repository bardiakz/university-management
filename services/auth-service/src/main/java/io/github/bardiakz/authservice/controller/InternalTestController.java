package io.github.bardiakz.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/test")
public class InternalTestController {

    @PostMapping("/user-registered")
    public ResponseEntity<String> userRegisteredTest() {
        return ResponseEntity.ok("OK");
    }
}
