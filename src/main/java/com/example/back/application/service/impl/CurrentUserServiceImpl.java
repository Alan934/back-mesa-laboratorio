package com.example.back.application.service.impl;

import com.example.back.application.service.CurrentUserService;
import com.example.back.domain.model.Role;
import com.example.back.domain.model.User;
import com.example.back.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class CurrentUserServiceImpl implements CurrentUserService {

    private final UserRepository userRepository;

    public CurrentUserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getOrCreateCurrentUser() {
        Jwt jwt = getJwt();
        String sub = jwt.getSubject();
        String email = Optional.ofNullable(jwt.getClaimAsString("email")).orElse(null);
        String givenName = Optional.ofNullable(jwt.getClaimAsString("given_name")).orElse(null);
        String familyName = Optional.ofNullable(jwt.getClaimAsString("family_name")).orElse(null);

        Role resolved = resolveRoleFromAuthorities();

        // Primero intentamos por auth0UserId (sub)
        Optional<User> bySub = userRepository.findByAuth0UserId(sub);
        if (bySub.isPresent()) {
            User u = bySub.get();
            // Sincronizar cambios básicos y rol
            if (email != null) u.setEmail(email);
            if (givenName != null) u.setFirstName(givenName);
            if (familyName != null) u.setLastName(familyName);
            if (u.getRole() != resolved) u.setRole(resolved);
            return u; // JPA sincroniza por @Transactional
        }
        // Si no existe y hay email, intentamos por email
        if (email != null) {
            Optional<User> byEmail = userRepository.findByEmail(email);
            if (byEmail.isPresent()) {
                User u = byEmail.get();
                u.setAuth0UserId(sub);
                if (u.getRole() != resolved) u.setRole(resolved);
                if (givenName != null) u.setFirstName(givenName);
                if (familyName != null) u.setLastName(familyName);
                return u;
            }
        }
        // Creamos el usuario con rol segun autoridades
        User user = new User();
        user.setAuth0UserId(sub);
        user.setEmail(email);
        user.setFirstName(givenName);
        user.setLastName(familyName);
        user.setRole(resolved);
        return userRepository.save(user);
    }

    private Role resolveRoleFromAuthorities() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals("ROLE_ADMIN"));
        if (isAdmin) return Role.ADMIN;
        boolean isPractitioner = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals("ROLE_PRACTITIONER"));
        if (isPractitioner) return Role.PRACTITIONER;
        return Role.CLIENT;
    }

    private Jwt getJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken token) {
            return token.getToken();
        }
        throw new IllegalStateException("JWT authentication is required");
    }
}
