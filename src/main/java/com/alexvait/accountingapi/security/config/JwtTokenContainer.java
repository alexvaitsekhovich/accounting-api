package com.alexvait.accountingapi.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtTokenContainer {

    @Value("${jwtTokenSecret}")
    private String jwtToken;

    public String getTokenSecret() {
        return jwtToken;
    }
}
