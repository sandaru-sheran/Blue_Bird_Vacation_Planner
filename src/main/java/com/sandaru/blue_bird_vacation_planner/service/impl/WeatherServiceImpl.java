package com.sandaru.blue_bird_vacation_planner.service.impl;

import com.sandaru.blue_bird_vacation_planner.dto.WeatherOverviewResponse;
import com.sandaru.blue_bird_vacation_planner.service.WeatherService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate; // Import the modern Date API

@Service
public class WeatherServiceImpl implements WeatherService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public WeatherServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    // Changed String travelDate to LocalDate travelDate
    @Override
    public WeatherOverviewResponse getWeatherOverview(double latitude, double longitude, LocalDate travelDate) throws Exception {
        
        LocalDate today = LocalDate.now();
        if (travelDate.isBefore(today)) {
            throw new IllegalArgumentException("Travel date cannot be in the past: " + travelDate);
        }
        if (travelDate.isAfter(today.plusDays(6))) {
            throw new IllegalArgumentException("Weather forecast is only available for the next 7 days (up to " + today.plusDays(6) + ").");
        }

        String url = UriComponentsBuilder.fromUriString("https://api.open-meteo.com/v1/forecast")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("daily", "weather_code,temperature_2m_max,temperature_2m_min")
                .queryParam("timezone", "auto")
                .toUriString();

        String jsonResponse = restTemplate.getForObject(url, String.class);
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode dailyNode = rootNode.path("daily");

        if (dailyNode.isMissingNode()) {
            throw new RuntimeException("Unable to fetch daily weather data.");
        }

        JsonNode timeArray = dailyNode.path("time");
        JsonNode weatherCodeArray = dailyNode.path("weather_code");
        JsonNode tempMaxArray = dailyNode.path("temperature_2m_max");
        JsonNode tempMinArray = dailyNode.path("temperature_2m_min");

        for (int i = 0; i < timeArray.size(); i++) {

            // Convert LocalDate to String for the comparison
            if (timeArray.get(i).asText().equals(travelDate.toString())) {
                int weatherCode = weatherCodeArray.get(i).asInt();
                double tempMax = tempMaxArray.get(i).asDouble();
                double tempMin = tempMinArray.get(i).asDouble();

                String condition = mapWmoCodeToDescription(weatherCode);
                double avgTemp = (tempMax + tempMin) / 2.0;

                return new WeatherOverviewResponse(condition, avgTemp, 0.0, jsonResponse);
            }
        }

        throw new IllegalArgumentException("Weather forecast is only available for the next 7 days. Date not found: " + travelDate);
    }

    private String mapWmoCodeToDescription(int code) {
        switch (code) {
            case 0: return "Clear sky";
            case 1: return "Mainly clear";
            case 2: return "Partly cloudy";
            case 3: return "Overcast";
            case 45: case 48: return "Fog";
            case 51: case 53: case 55: return "Drizzle";
            case 56: case 57: return "Freezing Drizzle";
            case 61: return "Slight Rain";
            case 63: return "Moderate Rain";
            case 65: return "Heavy Rain";
            case 66: case 67: return "Freezing Rain";
            case 71: case 73: case 75: return "Snow fall";
            case 77: return "Snow grains";
            case 80: case 81: case 82: return "Rain showers";
            case 85: case 86: return "Snow showers";
            case 95: return "Thunderstorm";
            case 96: case 99: return "Thunderstorm with hail";
            default: return "Unknown";
        }
    }
}