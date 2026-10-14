package com.sandaru.blue_bird_vacation_planner.service.impl;

import com.sandaru.blue_bird_vacation_planner.dto.PasswordUpdateRequest;
import com.sandaru.blue_bird_vacation_planner.entity.Administrator;
import com.sandaru.blue_bird_vacation_planner.dto.LogInResponse;
import com.sandaru.blue_bird_vacation_planner.dto.LoginRequest;
import com.sandaru.blue_bird_vacation_planner.repository.UserDetailsRepository;
import com.sandaru.blue_bird_vacation_planner.service.AuthService;
import com.sandaru.blue_bird_vacation_planner.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserDetailsRepository userDetailsRepository;

    @Override
    public LogInResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        String jwt = jwtService.generateToken(authentication.getName());
        return new LogInResponse(jwt, loginRequest.getUsername());
    }

    @Override
    public LogInResponse updatePassword(String authHeader, PasswordUpdateRequest request) {

        String username = jwtService.getUsernameFromToken(authHeader);


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        username,
                        request.getCurrentPassword()
                )
        );

        Administrator admin = userDetailsRepository.findByUsername(username);
        if (admin == null) {
            throw new UsernameNotFoundException("User not found");
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(14);
        admin.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));

        userDetailsRepository.save(admin);

        String newJwt = jwtService.generateToken(username);
        return new LogInResponse(newJwt, username);
    }
}
