package com.sandaru.blue_bird_vacation_planner.controller;

import com.sandaru.blue_bird_vacation_planner.config.CorsConfig;
import com.sandaru.blue_bird_vacation_planner.config.SecurityConfig;
import com.sandaru.blue_bird_vacation_planner.dto.DestinationRequest;
import com.sandaru.blue_bird_vacation_planner.dto.DestinationResponse;
import com.sandaru.blue_bird_vacation_planner.entity.Destination;
import com.sandaru.blue_bird_vacation_planner.entity.DestinationCategory;
import com.sandaru.blue_bird_vacation_planner.filter.JwtFilter;
import com.sandaru.blue_bird_vacation_planner.service.DestinationService;
import com.sandaru.blue_bird_vacation_planner.service.JwtService;
import com.sandaru.blue_bird_vacation_planner.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
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

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DestinationController.class)
@Import({SecurityConfig.class, CorsConfig.class, JwtFilter.class, GlobalExceptionHandler.class})
class DestinationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DestinationService destinationService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService customUserDetailsService;

    private DestinationResponse sampleResponse;
    private String validDestinationJson;

    @BeforeEach
    void setUp() {
        Destination destination = new Destination(
                1L,
                "Matara Star Fort",
                DestinationCategory.HERITAGE,
                5.9485,
                80.5353,
                LocalTime.of(8, 30),
                LocalTime.of(17, 0),
                List.of(),
                List.of()
        );
        sampleResponse = DestinationResponse.from(destination);

        validDestinationJson = """
                {
                    "name": "Matara Star Fort",
                    "category": "HERITAGE",
                    "latitude": 5.9485,
                    "longitude": 80.5353,
                    "openingHour": "08:30:00",
                    "closingHour": "17:00:00",
                    "imageUrls": []
                }
                """;
    }

    @Test
    @DisplayName("GET /api/destinations - Public List All (200 OK)")
    void getDestinations_WithoutCategory_ReturnsAllDestinations() throws Exception {
        when(destinationService.findAll(null)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/destinations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Matara Star Fort"))
                .andExpect(jsonPath("$[0].category").value("HERITAGE"));
    }

    @Test
    @DisplayName("GET /api/destinations?category=HERITAGE - Public Filtered List (200 OK)")
    void getDestinations_WithCategory_ReturnsFilteredDestinations() throws Exception {
        when(destinationService.findAll(DestinationCategory.HERITAGE)).thenReturn(List.of(sampleResponse));

        mockMvc.perform(get("/api/destinations").param("category", "HERITAGE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].category").value("HERITAGE"));
    }

    @Test
    @DisplayName("GET /api/destinations/{id} - Public Get By ID (200 OK)")
    void getDestination_WithExistingId_ReturnsDestination() throws Exception {
        when(destinationService.findById(1L)).thenReturn(sampleResponse);

        mockMvc.perform(get("/api/destinations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Matara Star Fort"));
    }

    @Test
    @DisplayName("GET /api/destinations/{id} - Non-Existent ID (404 Not Found)")
    void getDestination_WithNonExistentId_ReturnsNotFound() throws Exception {
        when(destinationService.findById(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination not found: 99"));

        mockMvc.perform(get("/api/destinations/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("POST /api/destinations - Admin Create Destination (201 Created)")
    void createDestination_AsAdmin_ReturnsCreated() throws Exception {
        when(destinationService.create(any(DestinationRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/destinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validDestinationJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Matara Star Fort"));
    }

    @Test
    @DisplayName("POST /api/destinations - Unauthenticated Create Blocked (403 Forbidden)")
    void createDestination_Unauthenticated_ReturnsForbidden() throws Exception {
        mockMvc.perform(post("/api/destinations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validDestinationJson))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("PUT /api/destinations/{id} - Admin Update Destination (200 OK)")
    void updateDestination_AsAdmin_ReturnsUpdatedDestination() throws Exception {
        when(destinationService.update(eq(1L), any(DestinationRequest.class))).thenReturn(sampleResponse);

        mockMvc.perform(put("/api/destinations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validDestinationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Matara Star Fort"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/destinations/{id} - Admin Delete Destination (204 No Content)")
    void deleteDestination_AsAdmin_ReturnsNoContent() throws Exception {
        doNothing().when(destinationService).delete(1L);

        mockMvc.perform(delete("/api/destinations/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("DELETE /api/destinations/{id} - Non-Existent ID (404 Not Found)")
    void deleteDestination_WithNonExistentId_ReturnsNotFound() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination not found: 99"))
                .when(destinationService).delete(99L);

        mockMvc.perform(delete("/api/destinations/99"))
                .andExpect(status().isNotFound());
    }
}