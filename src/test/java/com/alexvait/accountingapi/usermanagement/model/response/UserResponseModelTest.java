package com.alexvait.accountingapi.usermanagement.model.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
        assertEquals(userResponseModel1, userResponseModel1);
        assertEquals(userResponseModel1, userResponseModel2);
        assertNotEquals(userResponseModel1, userResponseModel3);
        assertNotEquals(userResponseModel2, userResponseModel3);
    }

    @Test
    @DisplayName("Test hashCode()")
    void testHashCode() {

        // arrange
        UserResponseModel userResponseModel1 = getTestUserResponseModel();
        UserResponseModel userResponseModel2 = getTestUserResponseModel();

        // act, assert
        assertEquals(userResponseModel1.hashCode(), userResponseModel1.hashCode());
        assertEquals(userResponseModel1.hashCode(), userResponseModel2.hashCode());
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