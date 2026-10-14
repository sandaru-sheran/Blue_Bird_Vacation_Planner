package com.sandaru.blue_bird_vacation_planner.service;

import com.sandaru.blue_bird_vacation_planner.dto.DiningInsightRequest;
import com.sandaru.blue_bird_vacation_planner.dto.DiningInsightResponse;

import java.util.List;

public interface DiningService {

    List<DiningInsightResponse> findByDestinationId(Long destinationId);

    DiningInsightResponse create(DiningInsightRequest request);

    DiningInsightResponse update(Long id, DiningInsightRequest request);

    void delete(Long id);
}
