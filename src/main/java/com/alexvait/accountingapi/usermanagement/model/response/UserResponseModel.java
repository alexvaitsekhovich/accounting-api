package com.alexvait.accountingapi.usermanagement.model.response;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "users", itemRelation = "user")
public class UserResponseModel {
    private String publicId;
    private String firstName;
    private String lastName;
    private String email;
}
