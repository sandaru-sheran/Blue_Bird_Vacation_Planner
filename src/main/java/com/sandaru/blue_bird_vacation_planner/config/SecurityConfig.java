package com.sandaru.blue_bird_vacation_planner.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.sandaru.blue_bird_vacation_planner.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserDetailsService customUserDetailsService;
    private final JwtFilter jwtFilter;

    public SecurityConfig(UserDetailsService customUserDetailsService, JwtFilter jwtFilter) {
        this.customUserDetailsService = customUserDetailsService;
        this.jwtFilter = jwtFilter;
    }

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
        return new BCryptPasswordEncoder(14);
    }

    /**
     * 3. Configure the HTTP Security Rules
     * This defines what is public, what is protected, and enforces JWT statelessness.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Automatically picks up your CorsConfig bean!
                .cors(Customizer.withDefaults())

                // Disable CSRF because we are using token-based authentication, not cookies
                .csrf(csrf -> csrf.disable())

                // Ensure the backend enforces a stateless session (no server-side sessions)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Define URL access rules
                .authorizeHttpRequests(auth -> auth
                        // 1. Public Auth & Health Endpoints
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers("/api/health").permitAll()

                        // 2. Swagger UI & OpenAPI Docs
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        // 3. Public Tourist Endpoints (Viewing places, checking weather, calculating routes)
                        .requestMatchers(HttpMethod.GET, "/api/destinations/**", "/api/dining/nearby/**", "/api/weather/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/route/calculate").permitAll()

                        // 4. Everything else requires a valid JWT token; @PreAuthorize on the controllers checks the ADMIN role
                        .anyRequest().authenticated()
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Pass the UserDetailsService directly into the constructor
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}