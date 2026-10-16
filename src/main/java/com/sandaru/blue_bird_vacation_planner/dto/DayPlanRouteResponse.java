package com.sandaru.blue_bird_vacation_planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DayPlanRouteResponse {
    private int finalOptimizedTimeSeconds;
    private int totalDistanceMeters;
    private String overviewPolyline;
    private List<Long> optimizedWaypointOrder;
    private List<RouteSegmentDetail> segmentDetails;
    private String rawJsonResponse;
}