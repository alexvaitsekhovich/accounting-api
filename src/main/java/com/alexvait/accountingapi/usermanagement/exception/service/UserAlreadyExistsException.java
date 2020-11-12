package com.alexvait.accountingapi.usermanagement.exception.service;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
