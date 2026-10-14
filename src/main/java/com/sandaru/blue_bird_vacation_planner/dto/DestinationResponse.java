package com.sandaru.blue_bird_vacation_planner.dto;

import com.sandaru.blue_bird_vacation_planner.entity.Destination;
import com.sandaru.blue_bird_vacation_planner.entity.DestinationCategory;

import java.time.LocalTime;

public record DestinationResponse(
        Long id,
        String name,
        DestinationCategory category,
        Double latitude,
        Double longitude,
        LocalTime openingHour,
        LocalTime closingHour,
        String diningInsights
) {
    public static DestinationResponse from(Destination destination) {
        return new DestinationResponse(
                destination.getId(),
                destination.getName(),
                destination.getCategory(),
                destination.getLatitude(),
                destination.getLongitude(),
                destination.getOpeningHour(),
                destination.getClosingHour(),
                destination.getDiningInsights()
        );
    }
}
