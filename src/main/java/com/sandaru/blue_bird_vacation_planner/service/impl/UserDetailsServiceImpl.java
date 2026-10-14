package com.sandaru.blue_bird_vacation_planner.service.impl;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserDetailsServiceImpl {

    UserDetails loadUserByUsername(String username);

}
