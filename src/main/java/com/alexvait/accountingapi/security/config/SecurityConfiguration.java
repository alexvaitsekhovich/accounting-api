package com.alexvait.accountingapi.security.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfiguration {

    private static String jwtToken;

    public static String getTokenSecret() {
        return jwtToken;
    }

    // Spring doesn't support @Value on static fields, so to keep all security
    // variables in one place, use setter for populating the static variable jwtToken
    @Value("${jwtTokenSecret}")
    public void setTokenSecret(String jwtTokenSecret) {
        SecurityConfiguration.jwtToken = jwtTokenSecret;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
