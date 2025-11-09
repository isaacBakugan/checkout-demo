package com.farmatodo.checkout.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * Filtro que valida la cabecera X-API-KEY en cada solicitud.
 * 
 * Rechaza cualquier request sin una clave válida definida en
 * app.security.apiKeys (application.yml).
 */
@Component
@Profile("!test") // desactiva el filtro cuando spring.profiles.active=test
public class ApiKeyFilter extends OncePerRequestFilter {

    private final Set<String> validKeys;

    // public ApiKeyFilter(@Value("${app.security.apiKeys}") String keysCsv) {
    //     this.validKeys = Set.of(keysCsv.split(","));
    // }
    public ApiKeyFilter(@Value("${app.security.apiKeys:test-key}") String keysCsv) {
    this.validKeys = Set.of(keysCsv.split(","));
}

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String apiKey = request.getHeader("X-API-KEY");

        if (apiKey == null || !validKeys.contains(apiKey)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid API key");
            return;
        }

        filterChain.doFilter(request, response);
    }

    protected boolean shouldNotFilter(jakarta.servlet.http.HttpServletRequest request) {
    String p = request.getRequestURI();
    return p.equals("/") ||
           p.equals("/index.html") ||
           p.startsWith("/js/") ||
           p.startsWith("/css/") ||
           p.startsWith("/assets/") ||
           p.equals("/favicon.ico") ||
           p.startsWith("/h2-console");
}
}


