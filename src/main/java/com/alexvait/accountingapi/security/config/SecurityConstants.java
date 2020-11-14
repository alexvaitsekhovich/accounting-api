package com.alexvait.accountingapi.security.config;

import com.alexvait.accountingapi.usermanagement.controller.UserController;
import com.alexvait.accountingapi.security.springcontext.SpringApplicationContextProvider;
import com.alexvait.accountingapi.security.utils.PropertiesReader;

public class SecurityConstants {
    public static final long EXPIRATION_TIME_MS_15_MINUTES = 1000L * 60 * 15;
    public static final long EXPIRATION_TIME_MS_HUNDRED_YEARS = 1000L * 60 * 60 * 24 * 365 * 100; // approx. 100 years

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String SIGNUP_URL = UserController.BASE_URL;
    public static final String LOGIN_URL = "/gettoken";

    public static final String READ_AUTHORITY = "CAN_READ";
    public static final String WRITE_AUTHORITY = "CAN_WRITE";
    public static final String DELETE_AUTHORITY = "CAN_DELETE";

    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_USER = "ROLE_USER";

    public static String getTokenSecret() {
        PropertiesReader propsReader = SpringApplicationContextProvider.getBean(PropertiesReader.class);
        return propsReader.getTokenSecret();
    }
}
