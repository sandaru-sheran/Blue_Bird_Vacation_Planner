package com.sandaru.blue_bird_vacation_planner.service.impl;

public interface jwtServiceImpl {

    public String generateToken(String username);

    public String getUsernameFromToken(String token);

    public boolean validateToken(String token);

    public Long getUserIdFromToken(String token);
}
