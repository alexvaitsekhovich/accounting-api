package com.alexvait.accountingapi.usermanagement.model.request;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserUpdateRequestModel {

    @NotNull(message = "First name is mandatory")
    private String firstName;

    @NotNull(message = "Last name is mandatory")
    private String lastName;
}
