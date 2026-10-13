package com.sandaru.blue_bird_vacation_planner.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogInResponse {

    private String token;
    private String type = "Bearer";
    private String username;

    public LogInResponse(String token, String username) {
        this.token = token;
        this.username = username;
    }
}