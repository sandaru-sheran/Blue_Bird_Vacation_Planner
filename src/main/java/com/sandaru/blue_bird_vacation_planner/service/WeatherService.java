package com.sandaru.blue_bird_vacation_planner.service;

import com.sandaru.blue_bird_vacation_planner.dto.WeatherOverviewResponse;

import java.time.LocalDate;

public interface WeatherService {
    WeatherOverviewResponse getWeatherOverview(double latitude, double longitude, LocalDate travelDate) throws Exception;
}