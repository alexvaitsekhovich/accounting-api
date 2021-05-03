package com.alexvait.accountingapi.security.config;

import org.springframework.beans.factory.annotation.Value;

public class JwtTokenContainer {

    @Value("${jwtTokenSecret}")
    private String jwtToken;

    public String getTokenSecret() {
        return jwtToken;
    }
}
