package com.example.back.application.service.impl;

import com.example.back.application.service.CurrentUserService;
import com.example.back.domain.model.Role;
import com.example.back.domain.model.User;
import com.example.back.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CurrentUserServiceImpl implements CurrentUserService {

    private static final Logger log = LoggerFactory.getLogger(CurrentUserServiceImpl.class);

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
        if (log.isDebugEnabled()) {
            log.debug("Provisioning current user. sub={}, email={}, role={}", sub, email, resolved);
        }

        // Primero por auth0UserId (sub)
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
        // Si no existe y hay email
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
        // Crear el usuario
        User user = new User();
        user.setAuth0UserId(sub);
        // Fallback de email si no viene en el access token (p.ej., Auth0 no incluye email en AT)
        user.setEmail(email != null ? email : generateFallbackEmail(sub));
        user.setFirstName(givenName);
        user.setLastName(familyName);
        user.setRole(resolved);
        return userRepository.save(user);
    }

    private String generateFallbackEmail(String sub) {
        // Genera un local-part seguro sustituyendo caracteres no válidos y limitando longitud
        String local = sub == null ? "user" : sub.replaceAll("[^A-Za-z0-9._%+-]", "_");
        if (local.length() > 64) {
            local = local.substring(0, 64);
        }
        return local + "@auth0.local";
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
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }
}
