package com.example.sto.core.request.handler;

import com.example.sto.core.request.exception.InvalidRequestStatusException;
import com.example.sto.core.request.exception.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.sto.core.request.handler.ExceptionResponseUtil.buildErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ExceptionResponseUtil.ErrorResponseFormat> handleNotFound(ResourceNotFoundException ex){
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(InvalidRequestStatusException.class)
    public ResponseEntity<ExceptionResponseUtil.ErrorResponseFormat> handleInvalidRequestStatus(InvalidRequestStatusException ex){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionResponseUtil.ErrorResponseFormat> handleConstraintViolation(ConstraintViolationException ex){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponseUtil.ErrorResponseFormat> handleValidation(MethodArgumentNotValidException ex){
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponseUtil.ErrorResponseFormat> handleException(Exception ex){
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Unexcepted error occured");
    }
}
