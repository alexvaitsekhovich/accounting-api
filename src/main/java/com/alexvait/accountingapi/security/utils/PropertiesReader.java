package com.alexvait.accountingapi.security.utils;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class PropertiesReader {

    private final Environment env;

    public PropertiesReader(Environment env) {
        this.env = env;
    }

    public String getProperty(String property) {
        return env.getProperty(property);
    }

    public String getTokenSecret() {
        return getProperty("jwtTokenSecret");
    }
}
