package com.alexvait.accountingapi.usermanagement.bootstrap;

import com.alexvait.accountingapi.accounting.entity.InvoiceEntity;
import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import com.alexvait.accountingapi.accounting.entity.enums.Payment;
import com.alexvait.accountingapi.accounting.repository.InvoiceRepository;
import com.alexvait.accountingapi.accounting.repository.PositionRepository;
import com.alexvait.accountingapi.security.config.SecurityConstants;
import com.alexvait.accountingapi.security.entity.AuthorityEntity;
import com.alexvait.accountingapi.security.entity.RoleEntity;
import com.alexvait.accountingapi.security.repository.AuthorityRepository;
import com.alexvait.accountingapi.security.repository.RoleRepository;
import com.alexvait.accountingapi.security.utils.RandomStringUtils;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import com.alexvait.accountingapi.usermanagement.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@Profile({"dev", "testing"})
public class UsersSetup implements CommandLineRunner {

    private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final InvoiceRepository invoiceRepository;
    private final PositionRepository positionRepository;
    private final PasswordEncoder passwordEncoder;

    public UsersSetup(AuthorityRepository authorityRepository, RoleRepository roleRepository,
                      UserRepository userRepository, PasswordEncoder passwordEncoder,
                      InvoiceRepository invoiceRepository, PositionRepository positionRepository) {
        this.authorityRepository = authorityRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.invoiceRepository = invoiceRepository;
        this.positionRepository = positionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        final Random randomGenerator = new SecureRandom();

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

        createUser("John", "Admin", "admin@api.com", "admin-pass", adminRole);
        List<UserEntity> users = IntStream.range(1, 12).mapToObj(i -> createDummyUser("john.doe" + i + "@api.com", userRole))
                .collect(Collectors.toList());

        UserEntity lastUser = users.get(users.size() - 1);
        List<InvoiceEntity> invoices = IntStream.range(1, 5).mapToObj(i -> createInvoice(lastUser, (i + "").repeat(20), randomGenerator.nextInt(1000)))
                .collect(Collectors.toList());

        IntStream.range(0, 20).forEach(i -> createPosition(lastUser, invoices.get(0), i + 100, i));
        IntStream.range(0, 10).forEach(i -> createPosition(lastUser, invoices.get(1), i + 100, i));

        IntStream.range(0, 10).forEach(i -> createPosition(lastUser, null, i + 100, i));
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


    private UserEntity createDummyUser(String email, RoleEntity role) {
        return createUser("John", "Doe", email, "user-pass", role);
    }

    private UserEntity createUser(String firstName, String lastName,
                                  String email, String password, RoleEntity role) {
        UserEntity user = new UserEntity();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPublicId(RandomStringUtils.randomAlphanumeric(40));
        user.setEncryptedPassword(passwordEncoder.encode(password));
        user.setRoles(Collections.singletonList(role));

        if (userRepository.findByEmail(user.getEmail()) != null) {
            return null;
        }

        return userRepository.save(user);
    }

    private InvoiceEntity createInvoice(UserEntity user, String number, long amount) {
        InvoiceEntity invoice = new InvoiceEntity();
        invoice.setNumber(number);
        invoice.setAmount(amount);
        invoice.setUser(user);

        return invoiceRepository.save(invoice);
    }

    private PositionEntity createPosition(UserEntity user, InvoiceEntity invoice, long amount, long customerId) {
        PositionEntity position = new PositionEntity();
        position.setAmount(amount);
        position.setPayment(Payment.CASH);
        position.setCustomerId(customerId);
        position.setInvoice(invoice);
        position.setUser(user);
        return positionRepository.save(position);
    }

}