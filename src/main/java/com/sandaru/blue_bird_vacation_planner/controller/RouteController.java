package com.sandaru.blue_bird_vacation_planner.controller;

import com.sandaru.blue_bird_vacation_planner.dto.DayPlanRouteResponse;
import com.sandaru.blue_bird_vacation_planner.dto.RouteCalculationRequest;
import com.sandaru.blue_bird_vacation_planner.service.RouteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/route")
@RequiredArgsConstructor
public class RouteController {

    private final RouteService routeService;

    @PostMapping("/calculate")
    public ResponseEntity<DayPlanRouteResponse> post(@Valid @RequestBody RouteCalculationRequest request) throws Exception {

        DayPlanRouteResponse routeResponse = routeService.calculateDayPlanRoute(request);
        return ResponseEntity.ok(routeResponse);
    }
}