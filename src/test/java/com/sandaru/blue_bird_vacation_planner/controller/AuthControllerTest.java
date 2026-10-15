package com.sandaru.blue_bird_vacation_planner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandaru.blue_bird_vacation_planner.config.CorsConfig;
import com.sandaru.blue_bird_vacation_planner.config.SecurityConfig;
import com.sandaru.blue_bird_vacation_planner.dto.LogInResponse;
import com.sandaru.blue_bird_vacation_planner.dto.LoginRequest;
import com.sandaru.blue_bird_vacation_planner.dto.PasswordUpdateRequest;
import com.sandaru.blue_bird_vacation_planner.filter.JwtFilter;
import com.sandaru.blue_bird_vacation_planner.service.AuthService;
import com.sandaru.blue_bird_vacation_planner.service.JwtService;
import com.sandaru.blue_bird_vacation_planner.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({SecurityConfig.class, CorsConfig.class, JwtFilter.class, GlobalExceptionHandler.class})
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/auth/login - Success (200 OK)")
    void login_WithValidCredentials_ReturnsToken() throws Exception {
        LoginRequest request = new LoginRequest("admin", "Admin@123");
        LogInResponse response = new LogInResponse("mocked.jwt.token", "admin");

        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mocked.jwt.token"))
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @DisplayName("POST /api/auth/login - Invalid Credentials (401 Unauthorized)")
    void login_WithInvalidCredentials_ReturnsUnauthorized() throws Exception {
        LoginRequest request = new LoginRequest("admin", "WrongPass");

        when(authService.authenticateUser(any(LoginRequest.class)))
                .thenThrow(new BadCredentialsException("Invalid username or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/auth/update-password - Success (200 OK)")
    void updatePassword_WithValidRequestAndAdminRole_ReturnsNewToken() throws Exception {
        PasswordUpdateRequest request = new PasswordUpdateRequest("OldPass@123", "NewSecure@123");
        LogInResponse response = new LogInResponse("new.jwt.token", "admin");

        when(authService.updatePassword(eq("Bearer old.jwt.token"), any(PasswordUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/api/auth/update-password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer old.jwt.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new.jwt.token"))
                .andExpect(jsonPath("$.username").value("admin"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/auth/update-password - Weak Password Fails Validation (400 Bad Request)")
    void updatePassword_WithWeakPassword_ReturnsBadRequest() throws Exception {
        PasswordUpdateRequest invalidRequest = new PasswordUpdateRequest("OldPass@123", "weak");

        mockMvc.perform(put("/api/auth/update-password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer old.jwt.token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT /api/auth/update-password - Unauthenticated User (403 Forbidden)")
    void updatePassword_WithoutAuthentication_ReturnsForbidden() throws Exception {
        PasswordUpdateRequest request = new PasswordUpdateRequest("OldPass@123", "NewSecure@123");

        mockMvc.perform(put("/api/auth/update-password")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}