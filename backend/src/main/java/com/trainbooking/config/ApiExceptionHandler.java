package com.trainbooking.config;

import com.trainbooking.auth.service.AuthenticationException;
import com.trainbooking.trainsearch.api.TrainProviderUnavailableException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(AuthenticationException.class)
    ResponseEntity<ApiError> handleAuthentication(AuthenticationException exception) {
        return ResponseEntity.status(exception.status())
                .body(new ApiError(exception.code(), exception.getMessage()));
    }

    @ExceptionHandler(TrainProviderUnavailableException.class)
    ResponseEntity<ApiError> handleUnavailableProvider(TrainProviderUnavailableException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiError("TRAIN_PROVIDER_UNAVAILABLE", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiError> handleInvalidRequest(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + " " + error.getDefaultMessage())
                .orElse("The submitted details are invalid.");
        return ResponseEntity.badRequest().body(new ApiError("INVALID_REQUEST", message));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException exception) {
        return ResponseEntity.badRequest()
                .body(new ApiError("INVALID_REQUEST", "One or more request values are invalid."));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(new ApiError("INVALID_REQUEST", exception.getMessage()));
    }

    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<ApiError> handleResponseStatus(ResponseStatusException exception) {
        String message = exception.getReason() == null ? "The request could not be completed." : exception.getReason();
        return ResponseEntity.status(exception.getStatusCode())
                .body(new ApiError("REQUEST_REJECTED", message));
    }

    @ExceptionHandler(DataAccessException.class)
    ResponseEntity<ApiError> handleDataStoreUnavailable(DataAccessException exception) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiError(
                        "DATA_STORE_UNAVAILABLE",
                        "Account storage is unavailable right now. Please retry later."));
    }
}
