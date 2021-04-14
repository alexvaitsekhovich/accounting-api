package com.alexvait.accountingapi.security.config.authentication;

import com.alexvait.accountingapi.security.model.UserPrincipal;

public interface AuthenticationFacade {
    UserPrincipal getAuthenticatedUser();
}
