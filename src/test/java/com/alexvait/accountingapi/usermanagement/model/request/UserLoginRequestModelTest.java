package com.alexvait.accountingapi.usermanagement.model.request;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserLoginRequestModelTest {
    @Test
    void testGetterSetter() {
        UserLoginRequestModel model1 = new UserLoginRequestModel();
        model1.setPassword("pass");
        model1.setEmail("mod1@api.com");

        UserLoginRequestModel model2 = new UserLoginRequestModel();
        model2.setPassword("pass");
        model2.setEmail("mod2@api.com");

        assertEquals(model1.getPassword(), model2.getPassword());
        assertNotEquals(model1.getEmail(), model2.getEmail());
    }
}