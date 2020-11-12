package com.alexvait.accountingapi.usermanagement.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateRequestModel {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
}
