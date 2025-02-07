package com.ecommerce.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(
            ApiException ex, 
            WebRequest request) {
        log.error("API Exception: ", ex);
        ErrorResponse error = ErrorResponse.builder()
            .message(ex.getMessage())
            .code(ex.getCode())
            .timestamp(LocalDateTime.now())
            .path(((ServletWebRequest) request).getRequest().getRequestURI())
            .build();
            
        return new ResponseEntity<>(error, ex.getStatus());
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, 
            WebRequest request) {
        log.error("Unexpected Exception: ", ex);
        ErrorResponse error = ErrorResponse.builder()
            .message("An unexpected error occurred")
            .code("INTERNAL_SERVER_ERROR")
            .timestamp(LocalDateTime.now())
            .path(((ServletWebRequest) request).getRequest().getRequestURI())
            .build();
            
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
} 