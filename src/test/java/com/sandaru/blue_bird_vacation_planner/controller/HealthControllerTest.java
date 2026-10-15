package com.sandaru.blue_bird_vacation_planner.controller;

import com.sandaru.blue_bird_vacation_planner.config.CorsConfig;
import com.sandaru.blue_bird_vacation_planner.config.SecurityConfig;
import com.sandaru.blue_bird_vacation_planner.filter.JwtFilter;
import com.sandaru.blue_bird_vacation_planner.service.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HealthController.class)
@Import({SecurityConfig.class, CorsConfig.class, JwtFilter.class})
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/health - Public Health Check (200 OK)")
    void healthCheck_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/health"))
                .andExpect(status().isOk());
    }
}