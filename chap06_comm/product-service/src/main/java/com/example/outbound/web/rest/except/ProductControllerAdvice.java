package com.example.outbound.web.rest.except;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductControllerAdvice {

    @ExceptionHandler(value = JsonProcessingException.class)
    public String jsonProcessingExceptionHandler(JsonProcessingException e) {
        return e.getMessage();
    }
}
