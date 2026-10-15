package com.sandaru.blue_bird_vacation_planner.service;

import com.sandaru.blue_bird_vacation_planner.dto.DayPlanRouteResponse;
import com.sandaru.blue_bird_vacation_planner.dto.RouteCalculationRequest;

import java.util.List;

public interface RouteService {

    public DayPlanRouteResponse calculateDayPlanRoute(RouteCalculationRequest request) throws Exception;
}
