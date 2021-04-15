package com.alexvait.accountingapi.security.config.authentication;

import com.alexvait.accountingapi.security.model.UserPrincipal;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFacadeImpl implements AuthenticationFacade {

    @Override
    public UserPrincipal getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (!(principal instanceof UserPrincipal)) {
            throw new AuthenticationCredentialsNotFoundException("No authenticated user is found in the SecurityContext");
        }

        return ((UserPrincipal) principal);
    }

}
