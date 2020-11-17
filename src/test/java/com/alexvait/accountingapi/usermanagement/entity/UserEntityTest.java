package com.alexvait.accountingapi.usermanagement.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test UserEntity")
class UserEntityTest {

    @Test
    @DisplayName("Test equals()")
    void testEquals() {
        // arrange
        String userName = "John";

        UserEntity userEntity1 = new UserEntity();
        UserEntity userEntity2 = new UserEntity();
        UserEntity userEntity3 = new UserEntity();

        userEntity1.setFirstName(userName);
        userEntity2.setFirstName(userName);
        userEntity3.setFirstName("X");

        // act, assert
        assertAll(
                "test entity equals",
                () -> assertEquals(userEntity1, userEntity1, "equals self failed"),
                () -> assertEquals(userEntity1, userEntity2, "equals failed"),
                () -> assertNotEquals(userEntity1, userEntity3, "not equals 1-3 failed"),
                () -> assertNotEquals(userEntity2, userEntity3, "not equals 2-3 failed")
        );
    }

    @Test
    @DisplayName("Test hashCode()")
    void testHashCode() {
        String userName = "John";

        UserEntity userEntity1 = new UserEntity();
        UserEntity userEntity2 = new UserEntity();

        userEntity1.setFirstName(userName);
        userEntity2.setFirstName(userName);

        assertAll(
                "test entity hashCode",
                () -> assertEquals(userEntity1.hashCode(), userEntity1.hashCode(), "hashCode self failed"),
                () -> assertEquals(userEntity1.hashCode(), userEntity2.hashCode(), "hashCode failed")
        );
    }
}