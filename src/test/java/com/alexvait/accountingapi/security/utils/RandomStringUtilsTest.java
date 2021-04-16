package com.alexvait.accountingapi.security.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test RandomStringUtilsTest")
class RandomStringUtilsTest {

    @Test
    @DisplayName("Test alphabetic string generation")
    void testRandomAlphabetic() {
        int length = 300;
        String randomString = RandomStringUtils.randomAlphabetic(length);
        String randomString2 = RandomStringUtils.randomAlphabetic(length);

        assertEquals(length, randomString.length());
        assertEquals(length, randomString2.length());

        String onlyCharsRegexp = "^[a-zA-Z]+$";
        assertTrue(randomString.matches(onlyCharsRegexp));
        assertTrue(randomString2.matches(onlyCharsRegexp));

        assertNotEquals(randomString2, randomString);
    }

    @Test
    @DisplayName("Test alphanumeric string generation")
    void testRandomAlphanumeric() {
        int length = 300;
        String randomString = RandomStringUtils.randomAlphanumeric(length);
        String randomString2 = RandomStringUtils.randomAlphanumeric(length);

        assertEquals(length, randomString.length());
        assertEquals(length, randomString2.length());

        String digitsAndCharsRegexp = "^[a-zA-Z0-9]+$";
        assertTrue(randomString.matches(digitsAndCharsRegexp));
        assertTrue(randomString2.matches(digitsAndCharsRegexp));

        assertNotEquals(randomString2, randomString);
    }

    @Test
    @DisplayName("Test numeric string generation")
    void testRandomNumeric() {
        int length = 300;
        String randomString = RandomStringUtils.randomNumeric(length);
        String randomString2 = RandomStringUtils.randomNumeric(length);

        assertEquals(length, randomString.length());
        assertEquals(length, randomString2.length());

        String onlyDigitsRegexp = "^[0-9]+$";
        assertTrue(randomString.matches(onlyDigitsRegexp));
        assertTrue(randomString2.matches(onlyDigitsRegexp));

        assertNotEquals(randomString2, randomString);
    }
}