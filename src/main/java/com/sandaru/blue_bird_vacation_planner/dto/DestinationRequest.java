package com.sandaru.blue_bird_vacation_planner.dto;

import com.sandaru.blue_bird_vacation_planner.entity.DestinationCategory;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalTime;
import java.util.List;

public record DestinationRequest(
        @NotBlank(message = "Destination name is required")
        @Size(max = 150, message = "Destination name cannot exceed 150 characters")
        String name,

        @NotNull(message = "Destination category is required")
        DestinationCategory category,

        @NotNull(message = "Latitude is required")
        @DecimalMin(value = "-90.0", message = "Latitude must be at least -90.0")
        @DecimalMax(value = "90.0", message = "Latitude cannot exceed 90.0")
        Double latitude,

        @NotNull(message = "Longitude is required")
        @DecimalMin(value = "-180.0", message = "Longitude must be at least -180.0")
        @DecimalMax(value = "180.0", message = "Longitude cannot exceed 180.0")
        Double longitude,

        @NotNull(message = "Opening hour is required")
        LocalTime openingHour,

        @NotNull(message = "Closing hour is required")
        LocalTime closingHour,

        @Size(max = 5, message = "You can only upload up to 5 images")
        List<String> imageUrls
) {
    // Automatically validates that closingHour is after openingHour
    @AssertTrue(message = "Closing hour must be after opening hour")
    public boolean isValidOperatingHours() {
        if (openingHour == null || closingHour == null) {
            return true; // Let @NotNull handle null values first
        }
        return closingHour.isAfter(openingHour);
    }
}