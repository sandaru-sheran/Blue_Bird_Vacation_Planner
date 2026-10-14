package com.sandaru.blue_bird_vacation_planner.controller;

import com.sandaru.blue_bird_vacation_planner.dto.PasswordUpdateRequest;
import com.sandaru.blue_bird_vacation_planner.dto.LogInResponse;
import com.sandaru.blue_bird_vacation_planner.dto.LoginRequest;
import com.sandaru.blue_bird_vacation_planner.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<LogInResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LogInResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LogInResponse> updatePassword(@RequestHeader(HttpHeaders.AUTHORIZATION) String authHeader, @Valid @RequestBody PasswordUpdateRequest request) {

        LogInResponse response = authService.updatePassword(authHeader, request);
        return ResponseEntity.ok(response);
    }
}
