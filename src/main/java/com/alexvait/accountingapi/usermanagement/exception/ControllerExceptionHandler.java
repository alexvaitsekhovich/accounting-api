package com.alexvait.accountingapi.usermanagement.exception;

import com.alexvait.accountingapi.usermanagement.exception.service.UserAlreadyExistsException;
import com.alexvait.accountingapi.usermanagement.model.response.ResponseOperationState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<OperationResponse> handleUserExistsException(UserAlreadyExistsException ex, HttpServletRequest httpRequest) {
        return constructResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<OperationResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest httpRequest) {
        return constructResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<OperationResponse> handleGenericException(Exception ex, HttpServletRequest httpRequest) {
        return constructResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), httpRequest.getRequestURI());
    }

    private ResponseEntity<OperationResponse> constructResponseEntity(HttpStatus httpStatus, String error, String uri) {
        return constructResponseEntity(httpStatus, Collections.singletonList(error), uri);
    }

    private ResponseEntity<OperationResponse> constructResponseEntity(HttpStatus httpStatus, List<String> errors, String uri) {
        OperationResponse operationResponse = new OperationResponse(ResponseOperationState.FAILURE, httpStatus);
        operationResponse.setMessages(errors);
        operationResponse.setPath(uri);
        return new ResponseEntity<>(operationResponse, httpStatus);
    }
}
