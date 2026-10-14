package com.sandaru.blue_bird_vacation_planner.service.impl;

import com.sandaru.blue_bird_vacation_planner.dto.DayPlanRouteResponse;
import com.sandaru.blue_bird_vacation_planner.entity.Destination;
import com.sandaru.blue_bird_vacation_planner.repository.DestinationRepository;
import com.sandaru.blue_bird_vacation_planner.service.RouteService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RouteServiceImpl implements RouteService {

    @Value("${maps.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final DestinationRepository destinationRepository;

    private static final int BUFFER_TIME_SECONDS = 900;

    public RouteServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper, DestinationRepository destinationRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.destinationRepository = destinationRepository;
    }

    public DayPlanRouteResponse calculateDayPlanRoute(List<Long> destinationIds) throws Exception {
        if (destinationIds == null || destinationIds.size() < 2) {
            throw new IllegalArgumentException("At least two destinations are required to calculate a route.");
        }

        // 1. Extract coordinates (ORS strictly requires [Longitude, Latitude] order)
        List<List<Double>> coordinatesList = new ArrayList<>();
        for (Long id : destinationIds) {
            Destination destination = destinationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Destination not found with ID: " + id));
            coordinatesList.add(Arrays.asList(destination.getLongitude(), destination.getLatitude()));
        }

        // 2. Build the JSON request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("coordinates", coordinatesList);

        // 3. Set up the Headers with the API Key
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 4. Call the new API URL from the dashboard deprecation notice
        String url = "https://api.heigit.org/openrouteservice/v2/directions/driving-car";
        String jsonResponse = restTemplate.postForObject(url, entity, String.class);

        // 5. Parse the ORS JSON Response
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode primaryRoute = rootNode.path("routes").get(0);
        JsonNode summary = primaryRoute.path("summary");

        int totalDistanceMeters = (int) summary.path("distance").asDouble();
        int totalRawTravelTimeSeconds = (int) summary.path("duration").asDouble();

        // ORS standard directions do not return an optimized waypoint order array by default,
        // so we preserve the sequential order requested by the frontend.
        List<Integer> sequentialWaypointOrder = new ArrayList<>();
        int waypointCount = destinationIds.size() - 2;
        for (int i = 0; i < waypointCount; i++) {
            sequentialWaypointOrder.add(i);
        }

        int numberOfTransitions = destinationIds.size() - 1;
        int totalBufferSeconds = numberOfTransitions * BUFFER_TIME_SECONDS;
        int finalOptimizedTimeSeconds = totalRawTravelTimeSeconds + totalBufferSeconds;

        String overviewPolyline = primaryRoute.path("geometry").asText();

        return new DayPlanRouteResponse(
                finalOptimizedTimeSeconds,
                totalDistanceMeters,
                overviewPolyline,
                sequentialWaypointOrder,
                jsonResponse
        );
    }
}