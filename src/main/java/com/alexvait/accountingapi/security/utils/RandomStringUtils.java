package com.alexvait.accountingapi.security.utils;

import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;

public class RandomStringUtils {
    private static final Random RANDOM = new SecureRandom();

    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String lower = upper.toLowerCase(Locale.ROOT);
    public static final String digits = "0123456789";

    public static String randomAlphabetic(int length) {
        return randomString(length, upper + lower);
    }

    public static String randomNumeric(int length) {
        return randomString(length, digits);
    }

    public static String randomAlphanumeric(int length) {
        return randomString(length, upper + lower + digits);
    }

    private static String randomString(int length, String symbols) {
        if (length < 1)
            throw new IllegalArgumentException("Length cannot be negative");

        char[] randomStringBuf = new char[length];
        char[] symbolsBuf = symbols.toCharArray();

        for (int i = 0; i < length; i++) {
            randomStringBuf[i] = symbolsBuf[RANDOM.nextInt(symbolsBuf.length)];
        }

        return new String(randomStringBuf);
    }
}
