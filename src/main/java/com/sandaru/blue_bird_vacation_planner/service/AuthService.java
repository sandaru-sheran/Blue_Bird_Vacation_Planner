package com.sandaru.blue_bird_vacation_planner.service;

import com.sandaru.blue_bird_vacation_planner.models.LogInResponse;
import com.sandaru.blue_bird_vacation_planner.models.LoginRequest;
import com.sandaru.blue_bird_vacation_planner.service.impl.AuthServiceImpl;
import com.sandaru.blue_bird_vacation_planner.service.impl.jwtServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthServiceImpl {
    private final AuthenticationManager authenticationManager;
    private final jwtServiceImpl jwtServise;


    @Override
    public LogInResponse authenticateUser(LoginRequest loginRequest) {
        // 1. Verify the password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        // 2. Generate the token
        String jwt = jwtServise.generateToken(authentication.getName());

        // 3. Return the data object
        return new LogInResponse(jwt, loginRequest.getUsername());
    }
}
