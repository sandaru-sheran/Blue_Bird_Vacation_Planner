package com.sandaru.blue_bird_vacation_planner.service;

public class jwtServise implements jwtServiseImpl {

    @Override
    public String generateToken(String username) {
        return null;
    }

    @Override
    public String getUsernameFromToken(String token) {
        return null;
    }

    @Override
    public boolean validateToken(String token) {
        return false;
    }

    @Override
    public Long getUserIdFromToken(String token) {
        return null;
    }
}
