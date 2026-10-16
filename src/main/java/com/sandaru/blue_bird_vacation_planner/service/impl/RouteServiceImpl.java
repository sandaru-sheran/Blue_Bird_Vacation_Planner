package com.sandaru.blue_bird_vacation_planner.service.impl;

import com.sandaru.blue_bird_vacation_planner.dto.DayPlanRouteResponse;
import com.sandaru.blue_bird_vacation_planner.dto.RouteCalculationRequest;
import com.sandaru.blue_bird_vacation_planner.dto.RouteSegmentDetail;
import com.sandaru.blue_bird_vacation_planner.entity.Destination;
import com.sandaru.blue_bird_vacation_planner.repository.DestinationRepository;
import com.sandaru.blue_bird_vacation_planner.service.RouteService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
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

    private static final int DEFAULT_BUFFER_TIME_SECONDS = 900;

    public RouteServiceImpl(RestTemplate restTemplate, ObjectMapper objectMapper, DestinationRepository destinationRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.destinationRepository = destinationRepository;
    }

    @Override
    public DayPlanRouteResponse calculateDayPlanRoute(RouteCalculationRequest request) throws Exception {
        List<Long> destinationIds = request.destinationIds();

        if (destinationIds == null || destinationIds.size() < 2) {
            throw new IllegalArgumentException("At least two destinations are required to calculate a route.");
        }

        int bufferTime = (request.bufferTimeSeconds() != null) ? request.bufferTimeSeconds() : DEFAULT_BUFFER_TIME_SECONDS;

        // 1. Fetch all destinations
        List<Destination> rawDestinations = new ArrayList<>();
        for (Long id : destinationIds) {
            Destination destination = destinationRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Destination not found with ID: " + id));
            rawDestinations.add(destination);
        }

        // 2. Optimize the order of destinations (Nearest-Neighbor starting from the first stop)
        List<Destination> optimizedDestinations = sortDestinationsOptimally(rawDestinations);

        // Extract the full sorted list of Destination IDs
        List<Long> optimizedDestinationIds = optimizedDestinations.stream()
                .map(Destination::getId)
                .toList();

        // 3. Extract coordinates (ORS strictly requires [Longitude, Latitude] order)
        List<List<Double>> coordinatesList = new ArrayList<>();
        List<Integer> radiusesList = new ArrayList<>();
        for (Destination destination : optimizedDestinations) {
            coordinatesList.add(Arrays.asList(destination.getLongitude(), destination.getLatitude()));
            radiusesList.add(999999999);
        }

        // 4. Build the JSON request body
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("coordinates", coordinatesList);
        requestBody.put("radiuses", radiusesList); // Included so ORS snaps off-road pins (e.g. Yala/Horton Plains)

        // 5. Set up the Headers with the API Key
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        // 6. Call the ORS API
        String url = "https://api.heigit.org/openrouteservice/v2/directions/driving-car";
        String jsonResponse = restTemplate.postForObject(url, entity, String.class);

        // 7. Parse the ORS JSON Response
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode primaryRoute = rootNode.path("routes").get(0);
        JsonNode summary = primaryRoute.path("summary");

        int totalDistanceMeters = (int) Math.round(summary.path("distance").asDouble());
        int totalRawTravelTimeSeconds = (int) Math.round(summary.path("duration").asDouble());

        // Extract segment details
        List<RouteSegmentDetail> segmentDetails = new ArrayList<>();
        JsonNode segmentsNode = primaryRoute.path("segments");
        if (segmentsNode.isArray()) {
            for (JsonNode segment : segmentsNode) {
                double distance = segment.path("distance").asDouble();
                double duration = segment.path("duration").asDouble();

                String instruction = "Proceed to next location";
                JsonNode stepsNode = segment.path("steps");
                if (stepsNode.isArray() && !stepsNode.isEmpty()) {
                    instruction = stepsNode.get(0).path("instruction").asText();
                }

                segmentDetails.add(new RouteSegmentDetail(distance, duration, instruction));
            }
        }

        // Apply the dynamic buffer time
        int numberOfTransitions = optimizedDestinations.size() - 1;
        int totalBufferSeconds = numberOfTransitions * bufferTime;
        int finalOptimizedTimeSeconds = totalRawTravelTimeSeconds + totalBufferSeconds;

        String overviewPolyline = primaryRoute.path("geometry").asText();

        return new DayPlanRouteResponse(
                finalOptimizedTimeSeconds,
                totalDistanceMeters,
                overviewPolyline,
                optimizedDestinationIds,
                segmentDetails,
                jsonResponse
        );
    }

    private List<Destination> sortDestinationsOptimally(List<Destination> rawDestinations) {
        if (rawDestinations.size() <= 2) {
            return rawDestinations;
        }

        List<Destination> unvisited = new ArrayList<>(rawDestinations);
        List<Destination> optimized = new ArrayList<>();

        Destination current = unvisited.remove(0);
        optimized.add(current);

        while (!unvisited.isEmpty()) {
            Destination nearest = null;
            double minDistance = Double.MAX_VALUE;
            for (Destination candidate : unvisited) {
                double dist = calculateHaversineDistance(
                        current.getLatitude(), current.getLongitude(),
                        candidate.getLatitude(), candidate.getLongitude()
                );
                if (dist < minDistance) {
                    minDistance = dist;
                    nearest = candidate;
                }
            }
            optimized.add(nearest);
            unvisited.remove(nearest);
            current = nearest;
        }
        return optimized;
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        final int EARTH_RADIUS_KM = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}