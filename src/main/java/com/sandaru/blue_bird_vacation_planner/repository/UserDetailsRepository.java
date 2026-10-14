package com.sandaru.blue_bird_vacation_planner.repository;

import com.sandaru.blue_bird_vacation_planner.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepository extends JpaRepository<Administrator, Long> {

    public Administrator findByUsername(String username);

    public boolean existsByUsername(String username);
}
