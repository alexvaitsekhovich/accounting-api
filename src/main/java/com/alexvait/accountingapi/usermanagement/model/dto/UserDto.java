package com.alexvait.accountingapi.usermanagement.model.dto;

import lombok.Data;

@Data
public class UserDto {
    private long id;
    private String publicId;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String encryptedPassword;
//    private Collection<String> roles = new HashSet<>();
}
