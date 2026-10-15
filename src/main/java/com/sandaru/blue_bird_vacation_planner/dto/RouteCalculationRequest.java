package com.sandaru.blue_bird_vacation_planner.dto;

import jakarta.validation.constraints.*;

import java.util.List;

public record RouteCalculationRequest(
        @NotEmpty(message = "Destination ID list cannot be empty")
        @Size(min = 2, message = "At least two destinations are required to calculate a route")
        List<@NotNull(message = "Destination ID cannot be null")
        @Positive(message = "Destination ID must be a positive number")
                Long> destinationIds,

        @PositiveOrZero(message = "Buffer time cannot be negative")
        Integer bufferTimeSeconds) {
}