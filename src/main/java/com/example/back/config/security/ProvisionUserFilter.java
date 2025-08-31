package com.example.back.config.security;

import com.example.back.application.service.CurrentUserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ProvisionUserFilter extends OncePerRequestFilter {

    private final CurrentUserService currentUserService;

    public ProvisionUserFilter(CurrentUserService currentUserService) {
        this.currentUserService = currentUserService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String path = request.getRequestURI();
            // Solo para rutas de la API protegidas
            if (path.startsWith("/api/")) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    // Provisiona el usuario si aún no existe
                    currentUserService.getOrCreateCurrentUser();
                }
            }
        } catch (Exception ignored) {
            // No bloquear la request por errores de provisionamiento
        }
        filterChain.doFilter(request, response);
    }
}
