package com.sandaru.blue_bird_vacation_planner.repository.Impl;

import com.sandaru.blue_bird_vacation_planner.entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDetailsRepositoryImpl extends JpaRepository<Administrator, Long> {

    public Administrator findByUsername(String username);

    public boolean existsByUsername(String username);
}
