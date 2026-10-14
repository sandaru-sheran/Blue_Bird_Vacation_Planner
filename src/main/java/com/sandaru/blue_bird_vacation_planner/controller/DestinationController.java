package com.sandaru.blue_bird_vacation_planner.controller;

import com.sandaru.blue_bird_vacation_planner.dto.DestinationRequest;
import com.sandaru.blue_bird_vacation_planner.dto.DestinationResponse;
import com.sandaru.blue_bird_vacation_planner.entity.DestinationCategory;
import com.sandaru.blue_bird_vacation_planner.service.DestinationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/destinations")
@RequiredArgsConstructor
public class DestinationController {

    private final DestinationService destinationService;

    @GetMapping
    public List<DestinationResponse> getDestinations(
            @RequestParam(required = false) DestinationCategory category) {
        return destinationService.findAll(category);
    }

    @GetMapping("/{id}")
    public DestinationResponse getDestination(@PathVariable Long id) {
        return destinationService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DestinationResponse> createDestination(
            @Valid @RequestBody DestinationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(destinationService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DestinationResponse updateDestination(
            @PathVariable Long id,
            @Valid @RequestBody DestinationRequest request) {
        return destinationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDestination(@PathVariable Long id) {
        destinationService.delete(id);
    }
}
