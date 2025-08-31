package com.example.back.application.service;

import com.example.back.domain.model.User;

// Servicio para resolver el usuario actual desde el token JWT de Auth0
public interface CurrentUserService {
    // Retorna el usuario actual y lo crea si no existe (provisionamiento automático)
    User getOrCreateCurrentUser();
}
