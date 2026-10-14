package com.sandaru.blue_bird_vacation_planner.controller;

import com.sandaru.blue_bird_vacation_planner.dto.DiningInsightRequest;
import com.sandaru.blue_bird_vacation_planner.dto.DiningInsightResponse;
import com.sandaru.blue_bird_vacation_planner.service.DiningService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/dining")
@RequiredArgsConstructor
public class DiningController {

    private final DiningService diningService;

    @GetMapping("/nearby/{destinationId}")
    public List<DiningInsightResponse> getNearbyDining(@PathVariable Long destinationId) {
        return diningService.findByDestinationId(destinationId);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DiningInsightResponse> createDining(@Valid @RequestBody DiningInsightRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(diningService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DiningInsightResponse updateDining(@PathVariable Long id, @Valid @RequestBody DiningInsightRequest request) {
        return diningService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDining(@PathVariable Long id) {
        diningService.delete(id);
    }
}
