package com.alexvait.accountingapi.security.model.request;

import lombok.Data;

@Data
public class UserCreateRequestModel {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
