package com.alexvait.accountingapi.usermanagement.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test UserDto")
class UserDtoTest {

    @Test
    @DisplayName("Test equals()")
    void testEquals() {
        // arrange
        UserDto userDto1 = getTestUserDto();
        UserDto userDto2 = getTestUserDto();

        UserDto userDto3 = getTestUserDto();
        userDto3.setEncryptedPassword("X");

        // act, assert
        assertAll(
                "test UserDto equals",
                () -> assertEquals(userDto1, userDto1, "equals failed"),
                () -> assertEquals(userDto1, userDto2, "equals 1-2 failed"),
                () -> assertNotEquals(userDto1, userDto3, "equals 1-3 failed"),
                () -> assertNotEquals(userDto2, userDto3, "not equals 2-3 failed")
        );
    }

    @Test
    @DisplayName("Test hashCode()")
    void testHashCode() {

        // arrange
        UserDto userDto1 = getTestUserDto();
        UserDto userDto2 = getTestUserDto();

        // act, assert
        // act, assert
        assertAll(
                "test UserDto hashCode",
                () -> assertEquals(userDto1.hashCode(), userDto1.hashCode(), "hashCode self failed"),
                () -> assertEquals(userDto1.hashCode(), userDto2.hashCode(), "hashCode failed")
        );

    }

    private UserDto getTestUserDto() {
        UserDto userDto = new UserDto();
        userDto.setId(1);
        userDto.setPublicId("12345");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john@gmail.com");
        userDto.setPassword("1234");
        userDto.setEncryptedPassword("asdsdfdsfsdfs");
        return userDto;
    }
}