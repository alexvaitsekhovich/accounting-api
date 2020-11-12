package com.alexvait.accountingapi.usermanagement.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
        assertEquals(userEntity1, userEntity1);
        assertEquals(userEntity1, userEntity2);
        assertNotEquals(userEntity1, userEntity3);
        assertNotEquals(userEntity2, userEntity3);
    }

    @Test
    @DisplayName("Test hashCode()")
    void testHashCode() {
        String userName = "John";

        UserEntity userEntity1 = new UserEntity();
        UserEntity userEntity2 = new UserEntity();

        userEntity1.setFirstName(userName);
        userEntity2.setFirstName(userName);

        assertEquals(userEntity1.hashCode(), userEntity1.hashCode());
        assertEquals(userEntity1.hashCode(), userEntity2.hashCode());
    }
}