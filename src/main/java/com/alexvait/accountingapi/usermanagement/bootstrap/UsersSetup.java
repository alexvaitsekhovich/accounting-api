package com.alexvait.accountingapi.usermanagement.bootstrap;

import com.alexvait.accountingapi.security.config.SecurityConstants;
import com.alexvait.accountingapi.security.entity.AuthorityEntity;
import com.alexvait.accountingapi.security.entity.RoleEntity;
import com.alexvait.accountingapi.security.repository.AuthorityRepository;
import com.alexvait.accountingapi.security.repository.RoleRepository;
import com.alexvait.accountingapi.security.utils.RandomStringUtils;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import com.alexvait.accountingapi.usermanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Component
public class UsersSetup implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersSetup(AuthorityRepository authorityRepository, RoleRepository roleRepository,
                      UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authorityRepository = authorityRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        AuthorityEntity readAuthority = createAuthority(SecurityConstants.READ_AUTHORITY);
        AuthorityEntity writeAuthority = createAuthority(SecurityConstants.WRITE_AUTHORITY);
        AuthorityEntity deleteAuthority = createAuthority(SecurityConstants.DELETE_AUTHORITY);

        RoleEntity adminRole = createRole(SecurityConstants.ROLE_ADMIN, Arrays.asList(readAuthority, writeAuthority, deleteAuthority));
        RoleEntity userRole = createRole(SecurityConstants.ROLE_USER, Arrays.asList(readAuthority, writeAuthority));

        if (userRole == null) {
            throw new RuntimeException("Cannot user admin role");
        }

        if (adminRole == null) {
            throw new RuntimeException("Cannot create admin role");
        }

        UserEntity admin = new UserEntity();
        admin.setFirstName("Admin");
        admin.setLastName("John");
        admin.setEmail("admin@api.com");
        admin.setPublicId(RandomStringUtils.randomAlphanumeric(40));
        admin.setEncryptedPassword(passwordEncoder.encode("pass"));
        admin.setRoles(Collections.singletonList(adminRole));
        createUser(admin);
    }

    @Transactional
    AuthorityEntity createAuthority(String name) {
        AuthorityEntity authority = authorityRepository.findByName(name);
        if (authority == null) {
            authority = new AuthorityEntity(name);
            authorityRepository.save(authority);
        }

        return authority;
    }

    @Transactional
    RoleEntity createRole(String name, Collection<AuthorityEntity> authorities) {
        RoleEntity role = roleRepository.findByName(name);
        if (role == null) {
            role = new RoleEntity(name);
            role.setAuthorities(authorities);
            roleRepository.save(role);
        }

        return role;
    }

    Optional<UserEntity> createUser(UserEntity user) {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            return Optional.empty();
        }

        return Optional.of(userRepository.save(user));
    }
}
