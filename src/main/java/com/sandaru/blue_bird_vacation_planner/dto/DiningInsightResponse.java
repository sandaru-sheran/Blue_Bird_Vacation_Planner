package com.sandaru.blue_bird_vacation_planner.dto;

import com.sandaru.blue_bird_vacation_planner.entity.DiningImage;
import com.sandaru.blue_bird_vacation_planner.entity.DiningInsight;

import java.util.List;

public record DiningInsightResponse(
        Long id,
        String restaurantName,
        String priceRange,
        String recommendedDishes,
        Long destinationId,
        List<String>imageUrls
) {
    public static DiningInsightResponse from(DiningInsight insight) {
        return new DiningInsightResponse(
                insight.getId(),
                insight.getRestaurantName(),
                insight.getPriceRange(),
                insight.getRecommendedDishes(),
                insight.getDestination().getId(),
                insight.getImages() == null
                        ? List.of()
                        : insight.getImages().stream().map(DiningImage::getImageUrl).toList()
        );
    }
}
