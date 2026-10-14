package com.sandaru.blue_bird_vacation_planner.seeder;

import com.sandaru.blue_bird_vacation_planner.entity.Administrator;
import com.sandaru.blue_bird_vacation_planner.repository.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserDetailsRepository userDetailsRepository;
    private final PasswordEncoder passwordEncoder;

    // The #{} syntax tells Spring to parse the string as a Map
    @Value("#{${app.admin.credentials}}")
    private Map<String, String> adminCredentials;

    @Override
    public void run(String... args) throws Exception {
        if (userDetailsRepository.count() == 0) {

            // Iterate directly over the key-value pairs
            for (Map.Entry<String, String> entry : adminCredentials.entrySet()) {
                Administrator admin = new Administrator();
                admin.setUsername(entry.getKey());
                admin.setPassword(passwordEncoder.encode(entry.getValue()));
                admin.setRole("ADMIN");

                userDetailsRepository.save(admin);
                System.out.println("Provisioned admin account: " + entry.getKey());
            }
        }
    }
}