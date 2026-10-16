package com.sandaru.blue_bird_vacation_planner.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    /**
     * Comma-separated list of allowed origin patterns.
     * Override via environment variable: CORS_ALLOWED_ORIGINS
     * Example: https://myapp.com,https://admin.myapp.com
     */
    @Value("${cors.allowed-origins:*}")
    private String allowedOrigins;

    /**
     * Comma-separated list of allowed HTTP methods.
     * Override via environment variable: CORS_ALLOWED_METHODS
     */
    @Value("${cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private String allowedMethods;

    /**
     * Comma-separated list of allowed headers.
     * Override via environment variable: CORS_ALLOWED_HEADERS
     */
    @Value("${cors.allowed-headers:Authorization,Content-Type,Accept}")
    private String allowedHeaders;

    /**
     * Comma-separated list of exposed headers.
     * Override via environment variable: CORS_EXPOSED_HEADERS
     */
    @Value("${cors.exposed-headers:Authorization}")
    private String exposedHeaders;

    /**
     * Whether to allow credentials (cookies, Authorization header).
     * Override via environment variable: CORS_ALLOW_CREDENTIALS
     */
    @Value("${cors.allow-credentials:true}")
    private boolean allowCredentials;

    /**
     * Preflight cache duration in seconds.
     * Override via environment variable: CORS_MAX_AGE
     */
    @Value("${cors.max-age:3600}")
    private long maxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(splitToList(allowedOrigins));
        configuration.setAllowedMethods(splitToList(allowedMethods));
        configuration.setAllowedHeaders(splitToList(allowedHeaders));
        configuration.setExposedHeaders(splitToList(exposedHeaders));

        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(maxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> splitToList(String value) {
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}