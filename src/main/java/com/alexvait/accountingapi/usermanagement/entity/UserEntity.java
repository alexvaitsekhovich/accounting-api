package com.alexvait.accountingapi.usermanagement.entity;

import com.alexvait.accountingapi.security.entity.RoleEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashSet;

@Entity
@Table(name = "users")
@Getter
@Setter
@EqualsAndHashCode
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false, nullable = false)
    private long id;

    @Column(nullable = false, length = 40)
    private String publicId;

    @Column(nullable = false, length = 50)
    @Size(min = 2, max = 50)
    @NotNull(message = "First name is mandatory")
    private String firstName;

    @Column(nullable = false, length = 50)
    @Size(min = 2, max = 50)
    @NotNull(message = "Last name is mandatory")
    private String lastName;

    @Column(nullable = false, unique = true)
    @Size(min = 5, max = 100)
    @NotNull(message = "Email is mandatory")
    @Email(message = "Wrong email format")
    private String email;

    @Column(nullable = false)
    private String encryptedPassword;

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = @JoinColumn(name = "userId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id")
    )
    private Collection<RoleEntity> roles = new HashSet<>();

    @CreationTimestamp
    @EqualsAndHashCode.Exclude
    @Column(updatable = false)
    private Timestamp createdDate;

    @UpdateTimestamp
    @EqualsAndHashCode.Exclude
    private Timestamp lastModifiedDate;
}
