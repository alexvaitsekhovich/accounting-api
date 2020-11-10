package com.alexvait.accountingapi.security.model.request;

import lombok.Data;

@Data
public class UserUpdateRequestModel {
    private String firstName;
    private String lastName;
}
