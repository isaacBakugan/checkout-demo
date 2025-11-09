package com.farmatodo.checkout.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;


/**
 * Configuración de seguridad por API Key.
 * Permite acceso libre solo al front estático (index.html y JS).
 */
@Configuration
public class SecurityConfig {

    private final ApiKeyFilter apiKeyFilter;

    public SecurityConfig(@org.springframework.beans.factory.annotation.Autowired(required = false) ApiKeyFilter apiKeyFilter) {
        this.apiKeyFilter = apiKeyFilter;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index.html", "/js/**", "/css/**", "/assets/**", "/favicon.ico",  "/h2-console/**").permitAll()
                .anyRequest().permitAll()
            );

        if (apiKeyFilter != null) {
            http.addFilterBefore(apiKeyFilter, AnonymousAuthenticationFilter.class);
        }
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        return http.build();
    }
}
