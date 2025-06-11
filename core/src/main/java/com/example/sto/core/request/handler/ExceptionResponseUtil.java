package com.example.sto.core.request.handler;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionResponseUtil {

    @Getter
    @AllArgsConstructor
    public static class ErrorResponseFormat {
        private LocalDateTime timestamp;
        private String status;
        private String message;
    }

    public static ResponseEntity<ErrorResponseFormat> buildErrorResponse(HttpStatus status, String message) {
        ErrorResponseFormat errorResponseFormat = new ErrorResponseFormat(LocalDateTime.now(), status.name(), message);
        return ResponseEntity.status(status).body(errorResponseFormat);
    }
}