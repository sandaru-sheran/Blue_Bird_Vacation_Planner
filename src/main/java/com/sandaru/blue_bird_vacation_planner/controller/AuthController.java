package com.sandaru.blue_bird_vacation_planner.controller;

import com.sandaru.blue_bird_vacation_planner.models.LogInResponse;
import com.sandaru.blue_bird_vacation_planner.models.LoginRequest;
import com.sandaru.blue_bird_vacation_planner.service.AuthService;
import com.sandaru.blue_bird_vacation_planner.service.impl.AuthServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthServiceImpl authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LogInResponse> login(@RequestBody LoginRequest loginRequest) {
        LogInResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(response);
    }
}
