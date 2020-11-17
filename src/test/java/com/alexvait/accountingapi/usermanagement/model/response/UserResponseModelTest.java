package com.alexvait.accountingapi.usermanagement.model.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test UserResponseModel")
class UserResponseModelTest {

    @Test
    @DisplayName("Test equals()")
    void testEquals() {
        // arrange
        UserResponseModel userResponseModel1 = getTestUserResponseModel();
        UserResponseModel userResponseModel2 = getTestUserResponseModel();

        UserResponseModel userResponseModel3 = getTestUserResponseModel();
        userResponseModel3.setFirstName("X");

        // act, assert
        assertAll(
                "test UserResponseModel equals",
                () -> assertEquals(userResponseModel1, userResponseModel1, "equals self failed"),
                () -> assertEquals(userResponseModel1, userResponseModel2, "equals 1-2 failed"),
                () -> assertNotEquals(userResponseModel1, userResponseModel3, "not equals 1-3 failed"),
                () -> assertNotEquals(userResponseModel2, userResponseModel3, "not equals 2-3 failed")
        );
    }

    @Test
    @DisplayName("Test hashCode()")
    void testHashCode() {

        // arrange
        UserResponseModel userResponseModel1 = getTestUserResponseModel();
        UserResponseModel userResponseModel2 = getTestUserResponseModel();

        // act, assert
        assertAll(
                "test UserResponseModel equals",
                () -> assertEquals(userResponseModel1.hashCode(), userResponseModel1.hashCode(), "hashCode self failed"),
                () -> assertEquals(userResponseModel1.hashCode(), userResponseModel2.hashCode(), "hashCode failed")
        );
    }

    private UserResponseModel getTestUserResponseModel() {
        UserResponseModel userResponseModel = new UserResponseModel();
        userResponseModel.setPublicId("12345");
        userResponseModel.setFirstName("John");
        userResponseModel.setLastName("Doe");
        userResponseModel.setEmail("john@gmail.com");
        return userResponseModel;
    }

}