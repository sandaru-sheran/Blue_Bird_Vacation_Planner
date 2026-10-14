package com.sandaru.blue_bird_vacation_planner.dto;

import com.sandaru.blue_bird_vacation_planner.entity.DiningInsight;

public record DiningInsightResponse(
        Long id,
        String restaurantName,
        String priceRange,
        String recommendedDishes,
        Long destinationId
) {
    public static DiningInsightResponse from(DiningInsight insight) {
        return new DiningInsightResponse(
                insight.getId(),
                insight.getRestaurantName(),
                insight.getPriceRange(),
                insight.getRecommendedDishes(),
                insight.getDestination().getId()
        );
    }
}
