package com.sandaru.blue_bird_vacation_planner.service;

public interface JwtService {

    String generateToken(String username);

    String getUsernameFromToken(String token);

    boolean validateToken(String token);

    Long getUserIdFromToken(String token);
}
