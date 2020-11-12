package com.alexvait.accountingapi.usermanagement.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Size;

@Entity
@Table(name = "users")
@Data
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @Column(nullable = false, length = 40)
    private String publicId;

    @Column(nullable = false, length = 50)
    @Size(min = 2, max = 50)
    private String firstName;

    @Column(nullable = false, length = 50)
    @Size(min = 2, max = 50)
    private String lastName;

    @Column(nullable = false, unique = true)
    @Size(min = 5, max = 100)
    private String email;

    @Column(nullable = false)
    private String encryptedPassword;
}
