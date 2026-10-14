package com.sandaru.blue_bird_vacation_planner.repository;

import com.sandaru.blue_bird_vacation_planner.entity.DiningInsight;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiningInsightRepository extends JpaRepository<DiningInsight, Long> {

    List<DiningInsight> findAllByDestinationId(Long destinationId);
}
