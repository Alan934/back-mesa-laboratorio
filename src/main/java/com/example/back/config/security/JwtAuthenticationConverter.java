package com.example.back.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashSet;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Value("${auth0.roles-claim:roles}")
    private String rolesClaim;

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        // Extrae roles desde un claim configurado y los transforma en autoridades de Spring (prefijo ROLE_)
        Collection<GrantedAuthority> authorities = extractAuthorities(jwt);
        return new JwtAuthenticationToken(jwt, authorities, getPrincipalName(jwt));
    }

    private String getPrincipalName(Jwt jwt) {
        // Usamos el subject (sub) de Auth0 como nombre principal
        return jwt.getSubject();
    }

    private Collection<GrantedAuthority> extractAuthorities(Jwt jwt) {
        Object claim = jwt.getClaims().get(rolesClaim);
        var set = new HashSet<GrantedAuthority>();
        if (claim instanceof List<?> list) {
            set.addAll(list.stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(String::toUpperCase)
                    .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet()));
        } else if (claim instanceof Map<?, ?> map) {
            // Por si el claim viene como objeto { roles: [...] }
            Object inner = map.get("roles");
            if (inner instanceof List<?> innerList) {
                set.addAll(innerList.stream()
                        .filter(String.class::isInstance)
                        .map(String.class::cast)
                        .map(String::toUpperCase)
                        .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet()));
            }
        }
        // Fallback: si no hay roles en el token, tratamos al usuario como CLIENT por defecto
        if (set.isEmpty()) {
            set.add(new SimpleGrantedAuthority("ROLE_CLIENT"));
        }
        return set;
    }
}
