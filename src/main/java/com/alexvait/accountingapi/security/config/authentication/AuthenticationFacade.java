package com.alexvait.accountingapi.security.config.authentication;

import com.alexvait.accountingapi.security.model.UserPrincipal;

import java.util.Optional;

public interface AuthenticationFacade {
    Optional<UserPrincipal> getAuthenticatedUser();
}
