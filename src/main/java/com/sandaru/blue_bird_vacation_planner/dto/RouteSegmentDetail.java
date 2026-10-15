package com.sandaru.blue_bird_vacation_planner.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteSegmentDetail {
    private double distance;
    private double duration;
    private String instruction;
}
