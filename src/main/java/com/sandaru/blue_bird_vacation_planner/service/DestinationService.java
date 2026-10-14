package com.sandaru.blue_bird_vacation_planner.service;

import com.sandaru.blue_bird_vacation_planner.dto.DestinationRequest;
import com.sandaru.blue_bird_vacation_planner.dto.DestinationResponse;
import com.sandaru.blue_bird_vacation_planner.entity.DestinationCategory;

import java.util.List;

public interface DestinationService {

    List<DestinationResponse> findAll(DestinationCategory category);

    DestinationResponse findById(Long id);

    DestinationResponse create(DestinationRequest request);

    DestinationResponse update(Long id, DestinationRequest request);

    void delete(Long id);
}
