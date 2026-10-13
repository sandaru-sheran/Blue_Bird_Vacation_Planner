package com.sandaru.blue_bird_vacation_planner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * 1. Expose the AuthenticationManager
     * This allows your AuthService to inject it via the constructor.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    /**
     * 2. Configure the Password Encoder
     * Required so the AuthenticationManager knows how to compare the 
     * raw password with the hashed password in your database.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 3. Configure the HTTP Security Rules
     * This defines what is public, what is protected, and enforces JWT statelessness.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF because we are using token-based authentication, not cookies
                .csrf(csrf -> csrf.disable())

                // Ensure the backend enforces a stateless session (no server-side sessions)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define URL access rules
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/health").permitAll()

                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/destinations/**").permitAll()

                        // Require authentication for EVERYTHING else (like POST/PUT/DELETE destinations)
                        .anyRequest().authenticated()
                );

        // Note: You will later add your custom JwtRequestFilter here using:
        // http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}