package com.alexvait.accountingapi.security.model.response;

import lombok.Data;

@Data
public class UserResponseModel {
    private String publicId;
    private String firstName;
    private String lastName;
    private String email;
}
