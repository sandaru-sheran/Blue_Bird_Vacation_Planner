package com.sandaru.blue_bird_vacation_planner.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sandaru.blue_bird_vacation_planner.config.CorsConfig;
import com.sandaru.blue_bird_vacation_planner.config.SecurityConfig;
import com.sandaru.blue_bird_vacation_planner.dto.DiningInsightRequest;
import com.sandaru.blue_bird_vacation_planner.dto.DiningInsightResponse;
import com.sandaru.blue_bird_vacation_planner.filter.JwtFilter;
import com.sandaru.blue_bird_vacation_planner.service.DiningService;
import com.sandaru.blue_bird_vacation_planner.service.JwtService;
import com.sandaru.blue_bird_vacation_planner.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DiningController.class)
@Import({SecurityConfig.class, CorsConfig.class, JwtFilter.class, GlobalExceptionHandler.class})
class DiningControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private DiningService diningService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/dining/nearby/{destinationId} - Public Get Nearby Dining (200 OK)")
    void getNearbyDining_WithValidDestinationId_ReturnsList() throws Exception {
        DiningInsightResponse response = new DiningInsightResponse(
                10L, "Nupe Ambula", "LKR 800 - 2000", "ambul thiyal, red rice", 1L, List.of()
        );

        when(diningService.findByDestinationId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/dining/nearby/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].restaurantName").value("Nupe Ambula"))
                .andExpect(jsonPath("$[0].destinationId").value(1));
    }

    @Test
    @DisplayName("GET /api/dining/nearby/{destinationId} - Unknown Destination (404 Not Found)")
    void getNearbyDining_WithMissingDestination_ReturnsNotFound() throws Exception {
        when(diningService.findByDestinationId(999L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination not found: 999"));

        mockMvc.perform(get("/api/dining/nearby/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/dining - Admin Create Dining Insight (201 Created)")
    void createDining_AsAdmin_ReturnsCreated() throws Exception {
        DiningInsightRequest request = new DiningInsightRequest(
                "Nupe Ambula", "LKR 800 - 2000", "ambul thiyal, red rice", 1L, List.of()
        );
        DiningInsightResponse response = new DiningInsightResponse(
                10L, "Nupe Ambula", "LKR 800 - 2000", "ambul thiyal, red rice", 1L, List.of()
        );

        when(diningService.create(any(DiningInsightRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/dining")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.restaurantName").value("Nupe Ambula"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/dining - Blank Restaurant Name Fails Validation (400 Bad Request)")
    void createDining_WithBlankName_ReturnsBadRequest() throws Exception {
        DiningInsightRequest invalidRequest = new DiningInsightRequest(
                "", "LKR 800 - 2000", "ambul thiyal", 1L, List.of()
        );

        mockMvc.perform(post("/api/dining")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/dining/{id} - Admin Update Dining Insight (200 OK)")
    void updateDining_AsAdmin_ReturnsUpdatedInsight() throws Exception {
        DiningInsightRequest request = new DiningInsightRequest(
                "Nupe Ambula Updated", "LKR 1000 - 2500", "seafood curry", 1L, List.of()
        );
        DiningInsightResponse response = new DiningInsightResponse(
                10L, "Nupe Ambula Updated", "LKR 1000 - 2500", "seafood curry", 1L, List.of()
        );

        when(diningService.update(eq(10L), any(DiningInsightRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/dining/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.restaurantName").value("Nupe Ambula Updated"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/dining/{id} - Admin Delete Dining Insight (204 No Content)")
    void deleteDining_AsAdmin_ReturnsNoContent() throws Exception {
        doNothing().when(diningService).delete(10L);

        mockMvc.perform(delete("/api/dining/10"))
                .andExpect(status().isNoContent());
    }
}