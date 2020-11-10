package com.alexvait.accountingapi.security.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test RandomStringUtilsTest")
class RandomStringUtilsTest {

    private final String onlyCharsRegexp = "^[a-zA-Z]+$";
    private final String digitsAndCharsRegexp = "^[a-zA-Z0-9]+$";

    @Test
    @DisplayName("Test alphabetic string generation")
    void randomAlphabetic() {
        int length = 300;
        String randomString = RandomStringUtils.randomAlphabetic(length);

        assertEquals(length, randomString.length());
        assertTrue(randomString.matches(onlyCharsRegexp));
    }

    @Test
    @DisplayName("Test alphanumeric string generation")
    void randomAlphanumeric() {
        int length = 300;
        String randomString = RandomStringUtils.randomAlphanumeric(length);

        assertEquals(length, randomString.length());
        assertTrue(randomString.matches(digitsAndCharsRegexp));
    }
}