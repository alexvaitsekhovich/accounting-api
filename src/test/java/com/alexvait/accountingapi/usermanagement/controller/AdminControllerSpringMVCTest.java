package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.helpers.UserTestObjectGenerator;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@DisplayName("Test AdminController with SpringBootTest and MockMvc")
@ActiveProfiles("testing")
class AdminControllerSpringMVCTest {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void tearDown() {
        reset(userService);
    }

    @Test
    @DisplayName("Test get users with default pagination")
    void testGetUsersWithDefaultParameters() throws Exception {
        // arrange
        int defaultPageNumber = Integer.parseInt(AdminController.defaultPageNumber);
        int defaultPageSize = Integer.parseInt(AdminController.defaultPageSize);

        // generate stubs
        List<UserDto> usersDto = new ArrayList<>();

        final ArgumentCaptor<Integer> pageNumberCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<Integer> pageSizeCaptor = ArgumentCaptor.forClass(Integer.class);
        when(userService.getUsers(pageNumberCaptor.capture(), pageSizeCaptor.capture())).thenReturn(usersDto);

        // act
        mockMvc.perform(get(AdminController.BASE_URL + "/user"))
                .andExpect(status().isOk())
                .andDo(print());

        // assert

        assertEquals(defaultPageNumber, pageNumberCaptor.getValue());
        assertEquals(defaultPageSize, pageSizeCaptor.getValue());

        verify(userService).getUsers(defaultPageNumber, defaultPageSize);
        verifyNoMoreInteractions(userService);
    }

    @ParameterizedTest(name = "getUsers({arguments}])")
    @DisplayName("Test get users with pagination")
    @CsvSource({"0, 1", "0, 20", "10, 4", "10, 100"})
    void testGetUsersWithPaginationParameters(int pageNumber, int pageSize) throws Exception {
        // arrange

        // generate stubs
        List<UserDto> usersDto = Stream
                .generate(UserTestObjectGenerator::createTestUserDto)
                .limit(pageSize)
                .collect(Collectors.toList());

        final ArgumentCaptor<Integer> pageNumberCaptor = ArgumentCaptor.forClass(Integer.class);
        final ArgumentCaptor<Integer> pageSizeCaptor = ArgumentCaptor.forClass(Integer.class);
        when(userService.getUsers(pageNumberCaptor.capture(), pageSizeCaptor.capture())).thenReturn(usersDto);

        // act
        mockMvc.perform(get(AdminController.BASE_URL + "/user?page={pageNumber}&size={pageSize}", pageNumber, pageSize)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$..users.length()").value(pageSize));
        // assert

        assertEquals(pageNumber, pageNumberCaptor.getValue());
        assertEquals(pageSize, pageSizeCaptor.getValue());

        verify(userService).getUsers(pageNumber, pageSize);
        verifyNoMoreInteractions(userService);
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
        verify(userService).deleteUserByPublicId(anyString());
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
        verify(userService).deleteUserByPublicId(anyString());
    }
}