package com.alexvait.accountingapi.security.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "authorities")
@Getter
@Setter
@NoArgsConstructor
public class AuthorityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @Column(nullable = false, length = 40)
    private String name;

    @ManyToMany(mappedBy = "authorities")
    private Set<RoleEntity> roles;

    public AuthorityEntity(String name) {
        this.name = name;
    }
}
