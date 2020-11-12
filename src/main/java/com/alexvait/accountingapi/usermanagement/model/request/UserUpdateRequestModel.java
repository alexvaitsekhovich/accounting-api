package com.alexvait.accountingapi.usermanagement.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequestModel {
    private String firstName;
    private String lastName;
}
