package com.sandaru.blue_bird_vacation_planner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandaru.blue_bird_vacation_planner.config.CorsConfig;
import com.sandaru.blue_bird_vacation_planner.config.SecurityConfig;
import com.sandaru.blue_bird_vacation_planner.dto.DayPlanRouteResponse;
import com.sandaru.blue_bird_vacation_planner.dto.RouteCalculationRequest;
import com.sandaru.blue_bird_vacation_planner.filter.JwtFilter;
import com.sandaru.blue_bird_vacation_planner.service.JwtService;
import com.sandaru.blue_bird_vacation_planner.service.RouteService;
import com.sandaru.blue_bird_vacation_planner.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RouteController.class)
@Import({SecurityConfig.class, CorsConfig.class, JwtFilter.class, GlobalExceptionHandler.class})
class RouteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RouteService routeService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService customUserDetailsService;

    @Test
    @DisplayName("POST /api/route/calculate - Valid 2+ Destinations (200 OK)")
    void calculateRoute_WithValidDestinationIds_ReturnsRouteResponse() throws Exception {
        List<Long> destinationIds = List.of(1L, 2L, 3L);
        RouteCalculationRequest request = new RouteCalculationRequest(destinationIds);

        DayPlanRouteResponse mockResponse = new DayPlanRouteResponse(
                3600,
                12500,
                "encoded_polyline_string",
                List.of(0),
                "{\"routes\":[]}"
        );

        when(routeService.calculateDayPlanRoute(destinationIds)).thenReturn(mockResponse);

        mockMvc.perform(post("/api/route/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overviewPolyline").value("encoded_polyline_string"));
    }

    @Test
    @DisplayName("POST /api/route/calculate - Fewer Than 2 Destinations Fails Validation (400 Bad Request)")
    void calculateRoute_WithSingleDestination_ReturnsBadRequest() throws Exception {
        RouteCalculationRequest invalidRequest = new RouteCalculationRequest(List.of(1L));

        mockMvc.perform(post("/api/route/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/route/calculate - Missing Destination Throws IllegalArgumentException (400 Bad Request)")
    void calculateRoute_WithNonExistentDestination_ReturnsBadRequest() throws Exception {
        List<Long> ids = List.of(1L, 999L);
        RouteCalculationRequest request = new RouteCalculationRequest(ids);

        when(routeService.calculateDayPlanRoute(ids))
                .thenThrow(new IllegalArgumentException("Destination not found with ID: 999"));

        mockMvc.perform(post("/api/route/calculate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Destination not found with ID: 999"));
    }
}