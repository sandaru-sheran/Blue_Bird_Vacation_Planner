package com.sandaru.blue_bird_vacation_planner.service.impl;

import com.sandaru.blue_bird_vacation_planner.dto.DiningInsightRequest;
import com.sandaru.blue_bird_vacation_planner.dto.DiningInsightResponse;
import com.sandaru.blue_bird_vacation_planner.entity.Destination;
import com.sandaru.blue_bird_vacation_planner.entity.DiningInsight;
import com.sandaru.blue_bird_vacation_planner.repository.DestinationRepository;
import com.sandaru.blue_bird_vacation_planner.repository.DiningInsightRepository;
import com.sandaru.blue_bird_vacation_planner.service.DiningService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiningServiceImpl implements DiningService {

    private final DiningInsightRepository diningInsightRepository;
    private final DestinationRepository destinationRepository;

    @Override
    public List<DiningInsightResponse> findByDestinationId(Long destinationId) {
        if (!destinationRepository.existsById(destinationId)) {
            throw destinationNotFound(destinationId);
        }
        return diningInsightRepository.findAllByDestinationId(destinationId)
                .stream()
                .map(DiningInsightResponse::from)
                .toList();
    }

    @Override
    public DiningInsightResponse create(DiningInsightRequest request) {
        return DiningInsightResponse.from(diningInsightRepository.save(toEntity(request)));
    }

    @Override
    public DiningInsightResponse update(Long id, DiningInsightRequest request) {
        DiningInsight insight = getInsight(id);
        insight.setRestaurantName(request.restaurantName());
        insight.setPriceRange(request.priceRange());
        insight.setRecommendedDishes(request.recommendedDishes());
        insight.setDestination(getDestination(request.destinationId()));
        return DiningInsightResponse.from(diningInsightRepository.save(insight));
    }

    @Override
    public void delete(Long id) {
        if (!diningInsightRepository.existsById(id)) {
            throw insightNotFound(id);
        }
        diningInsightRepository.deleteById(id);
    }

    private DiningInsight toEntity(DiningInsightRequest request) {
        DiningInsight insight = new DiningInsight();
        insight.setRestaurantName(request.restaurantName());
        insight.setPriceRange(request.priceRange());
        insight.setRecommendedDishes(request.recommendedDishes());
        insight.setDestination(getDestination(request.destinationId()));
        return insight;
    }

    private DiningInsight getInsight(Long id) {
        return diningInsightRepository.findById(id).orElseThrow(() -> insightNotFound(id));
    }

    private Destination getDestination(Long id) {
        return destinationRepository.findById(id)
                .orElseThrow(() -> destinationNotFound(id));
    }

    private ResponseStatusException insightNotFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Dining insight not found: " + id);
    }

    private ResponseStatusException destinationNotFound(Long id) {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Destination not found: " + id);
    }
}
