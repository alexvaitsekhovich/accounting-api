package com.alexvait.accountingapi.security.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "authorities")
@Data
public class AuthorityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @Column(nullable = false, length = 20)
    private String name;

    @ManyToMany(mappedBy = "authorities")
    private Collection<RoleEntity> roles;

    public AuthorityEntity() {
    }

    public AuthorityEntity(String name) {
        this.name = name;
    }
}
