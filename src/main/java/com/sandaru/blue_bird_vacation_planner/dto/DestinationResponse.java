package com.sandaru.blue_bird_vacation_planner.dto;

import com.sandaru.blue_bird_vacation_planner.entity.Destination;
import com.sandaru.blue_bird_vacation_planner.entity.DestinationCategory;
import com.sandaru.blue_bird_vacation_planner.entity.DestinationImage;

import java.time.LocalTime;
import java.util.List;

public record DestinationResponse(
        Long id,
        String name,
        DestinationCategory category,
        Double latitude,
        Double longitude,
        LocalTime openingHour,
        LocalTime closingHour,
        List<DiningInsightResponse> diningInsights,
        List<String> imageUrls
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
                destination.getDiningInsights() == null
                        ? List.of()
                        : destination.getDiningInsights().stream().map(DiningInsightResponse::from).toList(),
                destination.getImages() == null
                        ? List.of()
                        : destination.getImages().stream().map(DestinationImage::getImageUrl).toList()
        );
    }
}
