package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("Test access permission for Admin controller")
class AdminControllerPermissionsTest {

    protected MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebApplicationContext wac;

    String testUserPublicId;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        testUserPublicId = userRepository.findAll().get(3).getPublicId();
    }

    @Test
    @DisplayName("Get all users, without authentication")
    void listUsersNotAuth() throws Exception {
        mockMvc.perform(get(AdminController.BASE_URL + "/user"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("users-admin@api.com")
    @DisplayName("Get all users, with users admin")
    void listUsersByUsersAdmin() throws Exception {
        mockMvc.perform(get(AdminController.BASE_URL + "/user"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("super-admin@api.com")
    @DisplayName("Get all users, with super admin")
    void listUsersBySuperAdmin() throws Exception {
        mockMvc.perform(get(AdminController.BASE_URL + "/user"))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("john.doe1@api.com")
    @DisplayName("Get all users, with normal user")
    void listUsersByUser() throws Exception {
        mockMvc.perform(get(AdminController.BASE_URL + "/user"))
                .andExpect(status().isForbidden());
    }


    @Test
    @DisplayName("Delete a user, without authentication")
    void deleteUserNotAuth() throws Exception {
        mockMvc.perform(delete(AdminController.BASE_URL + "/user/" + testUserPublicId))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithUserDetails("users-admin@api.com")
    @DisplayName("Delete a user, with users admin")
    void deleteUserByUsersAdmin() throws Exception {
        mockMvc.perform(delete(AdminController.BASE_URL + "/user/" + testUserPublicId))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("super-admin@api.com")
    @DisplayName("Delete a user, with super admin")
    void deleteUserBySuperAdmin() throws Exception {
        mockMvc.perform(delete(AdminController.BASE_URL + "/user/" + testUserPublicId))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("john.doe1@api.com")
    @DisplayName("Delete a user, with normal user")
    void deleteUserByUser() throws Exception {
        mockMvc.perform(delete(AdminController.BASE_URL + "/user/" + testUserPublicId))
                .andExpect(status().isForbidden());
    }
}