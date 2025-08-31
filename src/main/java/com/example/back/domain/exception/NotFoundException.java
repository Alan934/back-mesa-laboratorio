package com.example.back.domain.exception;

// Excepción para cuando no se encuentra un recurso
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
