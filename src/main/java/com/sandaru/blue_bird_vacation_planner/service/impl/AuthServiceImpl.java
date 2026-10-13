package com.sandaru.blue_bird_vacation_planner.service.impl;

import com.sandaru.blue_bird_vacation_planner.models.LogInResponse;
import com.sandaru.blue_bird_vacation_planner.models.LoginRequest;

public interface AuthServiceImpl {

    public LogInResponse authenticateUser(LoginRequest loginRequest);

}
