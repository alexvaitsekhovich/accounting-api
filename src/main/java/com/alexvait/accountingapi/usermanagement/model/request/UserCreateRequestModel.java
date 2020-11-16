package com.alexvait.accountingapi.usermanagement.model.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Schema(name = "UserCreateModel", description = "Model for a new user")
public class UserCreateRequestModel {

    @NotNull(message = "First name is mandatory")
    private String firstName;

    @NotNull(message = "Last name is mandatory")
    private String lastName;

    @NotNull(message = "Email is mandatory")
    @Email(message = "Wrong email format")
    private String email;

    @NotNull(message = "Password is mandatory")
    @Length(min = 5, max = 50, message = "Password length must be between 5 and 50")
    @Schema(minLength = 5, maxLength = 50)
    private String password;
}
