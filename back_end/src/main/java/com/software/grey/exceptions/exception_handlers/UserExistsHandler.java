package com.software.grey.exceptions.exception_handlers;

import com.software.grey.exceptions.UserExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class UserExistsHandler {

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<String> handleUserExistsException(UserExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists");
    }

}
