package com.alexvait.accountingapi.security.exception.handler;

import com.alexvait.accountingapi.usermanagement.exception.service.UserAlreadyExistsException;
import com.alexvait.accountingapi.usermanagement.model.response.OperationResponse;
import com.alexvait.accountingapi.usermanagement.model.response.ResponseOperationState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<OperationResponse> handleUserExistsException(UserAlreadyExistsException ex, HttpServletRequest httpRequest) {
        log.error(ex.getMessage(), ex);
        return constructResponseEntity(HttpStatus.CONFLICT, ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<OperationResponse> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest httpRequest) {
        log.error(ex.getMessage(), ex);
        return constructResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<OperationResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest httpRequest) {

        log.error(ex.getMessage(), ex);

        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return constructResponseEntity(HttpStatus.BAD_REQUEST, errors, httpRequest.getRequestURI());
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<OperationResponse> handleGenericException(Exception ex, HttpServletRequest httpRequest) {
        log.error(ex.getMessage(), ex);
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
