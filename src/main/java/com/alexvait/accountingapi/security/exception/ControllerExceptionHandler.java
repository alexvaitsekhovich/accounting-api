package com.alexvait.accountingapi.security.exception;

import com.alexvait.accountingapi.security.exception.service.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleUserExistsException(UserAlreadyExistsException ex, HttpServletRequest httpRequest) {
        return constructResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest httpRequest) {
        return constructResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex, HttpServletRequest httpRequest) {
        return constructResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), httpRequest.getRequestURI());
    }


    private ResponseEntity<Map<String, Object>> constructResponseEntity(HttpStatus httpStatus, String error, String uri) {
        return constructResponseEntity(httpStatus, Collections.singletonList(error), uri);
    }

    private ResponseEntity<Map<String, Object>> constructResponseEntity(HttpStatus httpStatus, List<String> errors, String uri) {
        ApiError apiError = new ApiError(httpStatus);
        apiError.setErrors(errors);
        apiError.setPath(uri);
        return new ResponseEntity<>(apiError.getErrorBody(), httpStatus);
    }
}
