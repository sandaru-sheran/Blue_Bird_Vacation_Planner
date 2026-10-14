package com.sandaru.blue_bird_vacation_planner.controller;

import com.sandaru.blue_bird_vacation_planner.dto.WeatherOverviewResponse;
import com.sandaru.blue_bird_vacation_planner.dto.WeatherRequest;
import com.sandaru.blue_bird_vacation_planner.service.WeatherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    @GetMapping
    public ResponseEntity<WeatherOverviewResponse> getWeather(
            @ParameterObject @Valid @ModelAttribute WeatherRequest request) throws Exception {

        WeatherOverviewResponse response = weatherService.getWeatherOverview(
                request.lat(),
                request.lon(),
                request.travelDate()
        );

        return ResponseEntity.ok(response);
    }
}