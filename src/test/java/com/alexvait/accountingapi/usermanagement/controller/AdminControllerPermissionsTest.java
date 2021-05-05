package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.security.config.AuthorityConstants;
import com.alexvait.accountingapi.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Test access permission for Admin controller")
@Disabled
class AdminControllerPermissionsTest {

    protected MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    WebApplicationContext wac;

    String testUserPublicId;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("Get all users, without authentication")
    void listUsersNotAuth() throws Exception {
        mockMvc.perform(get(AdminController.BASE_URL + "/user"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("john.doe1@api.com")
    @DisplayName("Get all users, with normal user")
    void listUsersByUser() throws Exception {
        mockMvc.perform(get(AdminController.BASE_URL + "/user"))
                .andExpect(status().isForbidden());
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

    @Nested
    @DisplayName("Test permissions for deleting a user")
    class Delete {

        @BeforeEach
        public void setup() {
            testUserPublicId = userRepository.findAll().get(3).getPublicId();
        }

        @Test
        @Order(1)
        @DisplayName("Delete a user, without authentication")
        void deleteUserNotAuth() throws Exception {
            mockMvc.perform(delete(AdminController.BASE_URL + "/user/" + testUserPublicId))
                    .andExpect(status().isForbidden());
        }

        @Test
        @Order(2)
        @DisplayName("Delete a user, with normal user")
        @WithMockUser(authorities = {AuthorityConstants.USER_CREATE})
        void deleteUserByUser() throws Exception {
            mockMvc.perform(delete(AdminController.BASE_URL + "/user/" + testUserPublicId))
                    .andExpect(status().isForbidden());
        }

        @Test
        @Order(3)
        @DisplayName("Delete a user, with users admin")
        @WithMockUser(authorities = {AuthorityConstants.USER_DELETE})
        void deleteUserByUsersAdmin() throws Exception {
            mockMvc.perform(delete(AdminController.BASE_URL + "/user/" + testUserPublicId))
                    .andExpect(status().isOk());
        }
    }
}