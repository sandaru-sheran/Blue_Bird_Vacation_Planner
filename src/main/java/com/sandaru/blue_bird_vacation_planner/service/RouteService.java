package com.sandaru.blue_bird_vacation_planner.service;

import com.sandaru.blue_bird_vacation_planner.dto.DayPlanRouteResponse;

import java.util.List;

public interface RouteService {

    DayPlanRouteResponse calculateDayPlanRoute(List<Long> destinationIds) throws Exception;
}
