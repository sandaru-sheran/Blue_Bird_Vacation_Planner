package com.sandaru.blue_bird_vacation_planner.controller;

import com.sandaru.blue_bird_vacation_planner.config.CorsConfig;
import com.sandaru.blue_bird_vacation_planner.config.SecurityConfig;
import com.sandaru.blue_bird_vacation_planner.dto.WeatherOverviewResponse;
import com.sandaru.blue_bird_vacation_planner.filter.JwtFilter;
import com.sandaru.blue_bird_vacation_planner.service.JwtService;
import com.sandaru.blue_bird_vacation_planner.service.WeatherService;
import com.sandaru.blue_bird_vacation_planner.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WeatherController.class)
@Import({SecurityConfig.class, CorsConfig.class, JwtFilter.class, GlobalExceptionHandler.class})
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WeatherService weatherService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService customUserDetailsService;

    @Test
    @DisplayName("GET /api/weather - Valid Coordinates and Date (200 OK)")
    void getWeather_WithValidParams_ReturnsWeatherOverview() throws Exception {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        WeatherOverviewResponse response = new WeatherOverviewResponse(
                "Partly cloudy", 28.5, 0.0, "{\"daily\":{}}"
        );

        when(weatherService.getWeatherOverview(5.9485, 80.5353, tomorrow)).thenReturn(response);

        mockMvc.perform(get("/api/weather")
                        .param("lat", "5.9485")
                        .param("lon", "80.5353")
                        .param("travelDate", tomorrow.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.condition").value("Partly cloudy"))
                .andExpect(jsonPath("$.temperatureCelsius").value(28.5));
    }

    @Test
    @DisplayName("GET /api/weather - Invalid Latitude > 90 (400 Bad Request)")
    void getWeather_WithOutOfRangeLatitude_ReturnsBadRequest() throws Exception {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        mockMvc.perform(get("/api/weather")
                        .param("lat", "105.0")
                        .param("lon", "80.5353")
                        .param("travelDate", tomorrow.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/weather - Date Beyond 7-Day Window (400 Bad Request)")
    void getWeather_WithDateBeyondSevenDays_ReturnsBadRequest() throws Exception {
        LocalDate tenDaysOut = LocalDate.now().plusDays(10);

        when(weatherService.getWeatherOverview(5.9485, 80.5353, tenDaysOut))
                .thenThrow(new IllegalArgumentException("Weather forecast is only available for the next 7 days."));

        mockMvc.perform(get("/api/weather")
                        .param("lat", "5.9485")
                        .param("lon", "80.5353")
                        .param("travelDate", tenDaysOut.toString()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Weather forecast is only available for the next 7 days."));
    }
}