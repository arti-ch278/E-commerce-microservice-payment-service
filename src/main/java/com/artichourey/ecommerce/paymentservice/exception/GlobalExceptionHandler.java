package com.artichourey.ecommerce.paymentservice.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<?> handleNotFound(PaymentNotFoundException ex, HttpServletRequest request) {

        log.warn("Payment not found | path={} | message={}",
                request.getRequestURI(), ex.getMessage());

        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(PaymentAmountMismatchException.class)
    public ResponseEntity<?> handleAmountMismatch(PaymentAmountMismatchException ex, HttpServletRequest request) {

        log.warn("Payment amount mismatch | path={} | message={}",
                request.getRequestURI(), ex.getMessage());

        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {

        Map<String, String> fieldErrors = ex.getBindingResult().getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage()
                ));

        log.warn("Validation failed | path={} | errors={}",
                request.getRequestURI(), fieldErrors);

        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Validation Failed", request.getRequestURI());
        body.put("errors", fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraint(ConstraintViolationException ex, HttpServletRequest request) {

        Map<String, String> violations = ex.getConstraintViolations()
                .stream()
                .collect(Collectors.toMap(
                        v -> v.getPropertyPath().toString(),
                        v -> v.getMessage()
                ));

        log.warn("Constraint violation | path={} | violations={}",
                request.getRequestURI(), violations);

        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "Constraint Violation", request.getRequestURI());
        body.put("errors", violations);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex, HttpServletRequest request) {

        log.error("Unhandled exception | path={} | message={}",
                request.getRequestURI(), ex.getMessage(), ex); 

        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again later.",
                request.getRequestURI());
    }

    // Common response builder

    private ResponseEntity<?> buildResponse(HttpStatus status, String message, String path) {
        return new ResponseEntity<>(baseBody(status, message, path), status);
    }

    private Map<String, Object> baseBody(HttpStatus status, String message, String path) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);
        return body;
    }
}