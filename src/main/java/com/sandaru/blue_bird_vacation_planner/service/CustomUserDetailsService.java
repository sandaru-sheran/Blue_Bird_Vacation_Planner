package com.sandaru.blue_bird_vacation_planner.service;

import com.sandaru.blue_bird_vacation_planner.entity.Administrator;
import com.sandaru.blue_bird_vacation_planner.repository.Impl.UserDetailsRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDetailsRepositoryImpl userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Administrator admin = userRepository.findByUsername(username);

        if (admin == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // return useedetails object with username, password, and roles
        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .roles(admin.getRole())
                .build();
    }
}