package com.alexvait.accountingapi.security.entity;

import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import lombok.Data;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Table(name = "roles")
@Data
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @Column(nullable = false, length = 20)
    private String name;

    @ManyToMany(mappedBy = "roles")
    private Collection<UserEntity> users;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "role_authority",
            joinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authorityId", referencedColumnName = "id")
    )
    private Collection<AuthorityEntity> authorities;

    public RoleEntity() {
    }

    public RoleEntity(String name) {
        this.name = name;
    }
}
