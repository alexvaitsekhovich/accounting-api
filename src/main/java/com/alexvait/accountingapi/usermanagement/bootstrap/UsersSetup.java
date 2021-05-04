package com.alexvait.accountingapi.usermanagement.bootstrap;

import com.alexvait.accountingapi.accounting.entity.InvoiceEntity;
import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import com.alexvait.accountingapi.accounting.entity.enums.PositionPayment;
import com.alexvait.accountingapi.accounting.repository.InvoiceRepository;
import com.alexvait.accountingapi.accounting.repository.PositionRepository;
import com.alexvait.accountingapi.security.config.AuthorityConstants;
import com.alexvait.accountingapi.security.config.SecurityConstants;
import com.alexvait.accountingapi.security.entity.AuthorityEntity;
import com.alexvait.accountingapi.security.entity.RoleEntity;
import com.alexvait.accountingapi.security.repository.AuthorityRepository;
import com.alexvait.accountingapi.security.repository.RoleRepository;
import com.alexvait.accountingapi.security.utils.RandomStringUtils;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import com.alexvait.accountingapi.usermanagement.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
@Profile({"dev", "testing"})
public class UsersSetup implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;

    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private final InvoiceRepository invoiceRepository;

    private final PositionRepository positionRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        final Random randomGenerator = new SecureRandom();

        Set<String> userAdminAuthorities = Set.of(
                AuthorityConstants.MULTIPLE_USERS_READ, AuthorityConstants.USER_CREATE,
                AuthorityConstants.USER_READ, AuthorityConstants.USER_UPDATE,
                AuthorityConstants.USER_DELETE);

        Set<String> accountingAuthorities = Set.of(
                AuthorityConstants.MULTIPLE_INVOICES_READ, AuthorityConstants.INVOICE_GENERATE,
                AuthorityConstants.INVOICE_READ,
                AuthorityConstants.MULTIPLE_POSITIONS_READ, AuthorityConstants.POSITION_CREATE,
                AuthorityConstants.POSITION_READ, AuthorityConstants.LIST_PAYMENTS);

        //users admin
        UserEntity usersAdmin = createUserWithAuthorities(
                "Users", "Admin", "users-admin@api.com", "admin-pass",
                SecurityConstants.ROLE_USER_ADMIN, userAdminAuthorities.stream());

        if (usersAdmin == null) {
            throw new RuntimeException("Cannot create users admin");
        }

        // super admin, for both accounting and users
        UserEntity superAdmin = createUserWithAuthorities(
                "Super", "Admin", "super-admin@api.com", "admin-pass",
                SecurityConstants.ROLE_SUPER_ADMIN,
                Stream.concat(userAdminAuthorities.stream(), accountingAuthorities.stream()));

        if (superAdmin == null) {
            throw new RuntimeException("Cannot create super admin");
        }

        // users
        List<UserEntity> users = IntStream.range(1, 12)
                .mapToObj(i ->
                        createUserWithAuthorities("John", "Doe", "john.doe" + i + "@api.com", "user-pass",
                                SecurityConstants.ROLE_USER, accountingAuthorities.stream()))
                .collect(Collectors.toList());

        UserEntity lastUser = users.get(users.size() - 1);
        List<InvoiceEntity> invoices = IntStream.range(1, 5)
                .mapToObj(i -> createInvoice(lastUser, (i + "").repeat(20), randomGenerator.nextInt(1000)))
                .collect(Collectors.toList());

        IntStream.range(0, 20).forEach(i -> createPosition(lastUser, invoices.get(0), i + 100, i, "Position " + i));
        IntStream.range(0, 10).forEach(i -> createPosition(lastUser, invoices.get(1), i + 100, i, "Position " + i));

        IntStream.range(0, 10).forEach(i -> createPosition(lastUser, null, i + 100, i, "Unbilled position " + i));
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

    private UserEntity createUser(String firstName, String lastName,
                                  String email, String password, RoleEntity role) {
        UserEntity user = new UserEntity();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPublicId(RandomStringUtils.randomAlphanumeric(40));
        user.setEncryptedPassword(passwordEncoder.encode(password));
        user.setRoles(Set.of(role));

        if (userRepository.findByEmail(user.getEmail()) != null) {
            return null;
        }

        return userRepository.save(user);
    }

    private UserEntity createUserWithAuthorities(String firstName, String lastName,
                                                 String email, String password, String role,
                                                 Stream<String> authorities) {
        return createUser(firstName, lastName, email, password,
                createRole(role,
                        authorities
                                .map(this::createAuthority)
                                .collect(Collectors.toUnmodifiableSet())));
    }


    private InvoiceEntity createInvoice(UserEntity user, String number, long amount) {
        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setNumber(number);
        invoice.setAmount(amount);
        invoice.setUser(user);

        return invoiceRepository.save(invoice);
    }

    private PositionEntity createPosition(UserEntity user, InvoiceEntity invoice, long amount, long customerId, String label) {
        PositionEntity position = new PositionEntity();
        position.setAmount(amount);
        position.setPayment(PositionPayment.CASH);
        position.setCustomerId(customerId);
        position.setInvoice(invoice);
        position.setUser(user);
        position.setLabel(label);
        return positionRepository.save(position);
    }

}