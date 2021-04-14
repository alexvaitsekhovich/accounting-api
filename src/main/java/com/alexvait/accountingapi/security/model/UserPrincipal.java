package com.alexvait.accountingapi.security.model;

import com.alexvait.accountingapi.security.entity.RoleEntity;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

public class UserPrincipal implements UserDetails {

    private final UserEntity userEntity;

    @Getter
    private final String publicId;

    @Getter
    private final long id;

    public UserPrincipal(UserEntity userEntity) {
        this.userEntity = userEntity;
        this.publicId = userEntity.getPublicId();
        this.id = userEntity.getId();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new HashSet<>();
        Collection<RoleEntity> roles = userEntity.getRoles();

        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
            role.getAuthorities().forEach(
                    authority -> authorities.add(new SimpleGrantedAuthority(authority.getName()))
            );
        });

        return authorities;
    }

    public UserEntity getUserEntity() {
        return userEntity;
    }

    @Override
    public String getPassword() {
        return userEntity.getEncryptedPassword();
    }

    @Override
    public String getUsername() {
        return userEntity.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
