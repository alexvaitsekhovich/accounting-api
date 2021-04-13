package com.alexvait.accountingapi.security.config.authentication;

import com.alexvait.accountingapi.security.model.UserPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade {

    @Override
    public Optional<UserPrincipal> getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return (principal instanceof UserPrincipal)
                ? Optional.of((UserPrincipal) principal)
                : Optional.empty();
    }

}
