package com.example.book_services.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(value = RuntimeException.class)
    ResponseEntity<String> handlingRuntimeException(RuntimeException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    ResponseEntity<String> handlingMethod(HttpRequestMethodNotSupportedException e) {
        return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<String> handlingValidation(MethodArgumentNotValidException e) {
        return ResponseEntity.badRequest().body(e.getFieldError().getDefaultMessage());
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<String> handleHttpClientError(HttpClientErrorException ex) {
        HttpResponseErrorCustomException errorResponse = new HttpResponseErrorCustomException(
                ex.getStatusCode().value(),
                "Client: " + ex.getClass().getSimpleName()
        );
        HttpStatus httpStatus = (HttpStatus) ex.getStatusCode();
        return ResponseEntity.status(httpStatus).body(errorResponse.getMessage());
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<String> handleHttpServerError(HttpServerErrorException ex) {
        HttpResponseErrorCustomException errorResponse = new HttpResponseErrorCustomException(
                ex.getStatusCode().value(),
                "Server: " + ex.getClass().getSimpleName()
        );
        HttpStatus httpStatus = (HttpStatus) ex.getStatusCode();
        return ResponseEntity.status(httpStatus).body(errorResponse.getMessage());
    }

}
