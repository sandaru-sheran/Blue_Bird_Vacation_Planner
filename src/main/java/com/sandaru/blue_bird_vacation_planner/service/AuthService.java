package com.sandaru.blue_bird_vacation_planner.service;

import com.sandaru.blue_bird_vacation_planner.dto.PasswordUpdateRequest;
import com.sandaru.blue_bird_vacation_planner.dto.LogInResponse;
import com.sandaru.blue_bird_vacation_planner.dto.LoginRequest;

public interface AuthService {

    LogInResponse authenticateUser(LoginRequest loginRequest);

    LogInResponse updatePassword(String authHeader, PasswordUpdateRequest request);
}
