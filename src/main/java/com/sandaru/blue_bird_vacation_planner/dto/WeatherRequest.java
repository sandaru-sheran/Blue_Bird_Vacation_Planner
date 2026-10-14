package com.sandaru.blue_bird_vacation_planner.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record WeatherRequest(
        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be >= -90")
        @DecimalMax(value = "90.0", message = "Latitude must be <= 90")
        Double lat,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be >= -180")
        @DecimalMax(value = "180.0", message = "Longitude must be <= 180")
        Double lon,

        @NotNull(message = "Travel date is required")
        @FutureOrPresent(message = "Travel date cannot be in the past")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // Ensures YYYY-MM-DD query param binding
        LocalDate travelDate
) {
}