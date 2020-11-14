package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.usermanagement.exception.ControllerExceptionHandler;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Admin controller with mockMvc")
@ActiveProfiles("testing")
class AdminControllerSpringMVCTest {

    @Mock
    private UserService userService;

    private ObjectMapper jsonMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        jsonMapper = new ObjectMapper();

        AdminController adminController = new AdminController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Test delete user")
    void testDeleteUser() throws Exception {
        // arrange

        // act
        mockMvc.perform(delete(AdminController.BASE_URL + "/user/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details.status").value("200"))
                .andExpect(jsonPath("$.responseState").value("SUCCESS"))
                .andExpect(jsonPath("$.httpStatus").value("OK"));

        // assert
        verify(userService, times(1)).deleteUserByPublicId(anyString());
    }

    @Test
    @DisplayName("Test delete user when no user was found")
    void testDeleteUserNotFound() throws Exception {
        // arrange
        doThrow(new UsernameNotFoundException("")).when(userService).deleteUserByPublicId(anyString());

        // act
        mockMvc.perform(delete(AdminController.BASE_URL + "/user/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details.status").value("400"))
                .andExpect(jsonPath("$.responseState").value("FAILURE"))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"));

        // assert
        verify(userService, times(1)).deleteUserByPublicId(anyString());
    }
}