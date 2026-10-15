package com.sandaru.blue_bird_vacation_planner.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record DiningInsightRequest(
        @NotBlank(message = "Restaurant name is required")
        @Size(max = 150, message = "Restaurant name cannot exceed 150 characters")
        String restaurantName,

        @Size(max = 50, message = "Price range cannot exceed 50 characters")
        String priceRange,

        @Size(max = 1000, message = "Recommended dishes cannot exceed 1000 characters")
        String recommendedDishes,

        @NotNull(message = "Destination ID is required")
        @Positive(message = "Destination ID must be a positive number")
        Long destinationId,

        @Size(max = 5, message = "You can only upload up to 5 images")
        List<String> imageUrls
) {
}