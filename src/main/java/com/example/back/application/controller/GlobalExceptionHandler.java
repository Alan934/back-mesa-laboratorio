package com.example.back.application.controller;

import com.example.back.domain.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.TransactionSystemException;

// Manejador global de excepciones para respuestas consistentes de la API
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        return build(req, HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex, HttpServletRequest req) {
        String message = ex instanceof MethodArgumentNotValidException manve
                ? manve.getBindingResult().getAllErrors().stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining(", "))
                : ex.getMessage();
        return build(req, HttpStatus.BAD_REQUEST, message != null ? message : "Bad request");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        return build(req, HttpStatus.BAD_REQUEST, message.isBlank() ? "Validation error" : message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(req, HttpStatus.BAD_REQUEST, "Malformed JSON request");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        String name = ex.getName();
        Class<?> type = ex.getRequiredType();
        String requiredType = type != null ? type.getSimpleName() : "required type";
        String message = String.format("Parameter '%s' has invalid value. Expected %s.", name, requiredType);
        return build(req, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, Object>> handleMissingParam(MissingServletRequestParameterException ex, HttpServletRequest req) {
        String message = String.format("Missing required parameter '%s'", ex.getParameterName());
        return build(req, HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return build(req, HttpStatus.METHOD_NOT_ALLOWED, ex.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, Object>> handleMediaType(HttpMediaTypeNotSupportedException ex, HttpServletRequest req) {
        return build(req, HttpStatus.UNSUPPORTED_MEDIA_TYPE, ex.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(DataIntegrityViolationException ex, HttpServletRequest req) {
        // No exponer detalles de la base de datos al cliente
        return build(req, HttpStatus.CONFLICT, "Data integrity violation");
    }

    // Unwrap de validaciones JPA -> 400
    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<Map<String, Object>> handleTxSystem(TransactionSystemException ex, HttpServletRequest req) {
        Throwable cause = ex.getMostSpecificCause();
        if (cause instanceof ConstraintViolationException cve) {
            String message = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            return build(req, HttpStatus.BAD_REQUEST, message.isBlank() ? "Validation error" : message);
        }
        return build(req, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    }

    // Mapear errores de seguridad de método (@PreAuthorize) a 403/401 en vez de 500
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(req, HttpStatus.FORBIDDEN, "Forbidden");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(AuthenticationException ex, HttpServletRequest req) {
        return build(req, HttpStatus.UNAUTHORIZED, "Unauthorized");
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> handleResponseStatus(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        String reason = ex.getReason() != null ? ex.getReason() : status.getReasonPhrase();
        return build(req, status, reason);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleServerError(Exception ex, HttpServletRequest req) {
        // Log detallado en el servidor, mensaje genérico al cliente
        log.error("Unexpected error processing request {} {}", req.getMethod(), req.getRequestURI(), ex);
        return build(req, HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error");
    }

    private ResponseEntity<Map<String, Object>> build(HttpServletRequest req, HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        if (req != null) {
            body.put("path", req.getRequestURI());
        }
        return ResponseEntity.status(status).headers(HttpHeaders.EMPTY).body(body);
    }
}
