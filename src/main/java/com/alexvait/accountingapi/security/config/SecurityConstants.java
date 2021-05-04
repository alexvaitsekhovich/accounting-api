package com.alexvait.accountingapi.security.config;

import com.alexvait.accountingapi.usermanagement.controller.UserController;

public class SecurityConstants {
    public static final long EXPIRATION_TIME_MS_15_MINUTES = 1000L * 60 * 15;

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String SIGNUP_URL = UserController.BASE_URL;
    public static final String LOGIN_URL = "/gettoken";

    public static final String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    public static final String ROLE_USER_ADMIN = "ROLE_USER_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";
}
