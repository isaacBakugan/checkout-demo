package com.farmatodo.checkout.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;

/**
 * Configuración de seguridad por API Key.
 * Permite acceso libre solo al front estático (index.html y JS).
 */
@Configuration
public class SecurityConfig {

    private final ApiKeyFilter apiKeyFilter;

    public SecurityConfig(ApiKeyFilter apiKeyFilter) {
        this.apiKeyFilter = apiKeyFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/js/**", "/css/**", "/assets/**", "/favicon.ico").permitAll()
                .anyRequest().permitAll()
            )
            .addFilterBefore(apiKeyFilter, AnonymousAuthenticationFilter.class)
            .build();
    }
}
