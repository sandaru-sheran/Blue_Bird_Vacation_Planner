package com.sandaru.blue_bird_vacation_planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherOverviewResponse {
    private String condition; // e.g., "clear sky", "light rain"
    private double temperatureCelsius;
    private double humidity;
    private String rawJsonResponse; // Optional: raw data for frontend fallback
}