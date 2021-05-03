package com.alexvait.accountingapi.accounting.exception.handler;

import com.alexvait.accountingapi.accounting.exception.AccessDeniedException;
import com.alexvait.accountingapi.accounting.exception.NotFoundException;
import com.alexvait.accountingapi.usermanagement.model.response.OperationResponse;
import com.alexvait.accountingapi.usermanagement.model.response.ResponseOperationState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
@Slf4j
public class AccountingControllerExceptionHandler {

    @ExceptionHandler(AuthenticationCredentialsNotFoundException.class)
    public ResponseEntity<OperationResponse> handleNotAuthenticatedException(AuthenticationCredentialsNotFoundException ex,
                                                                             HttpServletRequest httpRequest) {
        log.error(ex.getMessage(), ex);
        return constructResponseEntity(HttpStatus.UNAUTHORIZED, ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<OperationResponse> handleNotFoundException(NotFoundException ex, HttpServletRequest httpRequest) {
        log.error(ex.getMessage(), ex);
        return constructResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage(), httpRequest.getRequestURI());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<OperationResponse> handleAccessDeniedException(AccessDeniedException ex, HttpServletRequest httpRequest) {
        log.error(ex.getMessage(), ex);
        return constructResponseEntity(HttpStatus.FORBIDDEN, ex.getMessage(), httpRequest.getRequestURI());
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
