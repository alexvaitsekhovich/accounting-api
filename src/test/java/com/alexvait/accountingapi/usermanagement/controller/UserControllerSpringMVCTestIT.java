package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.usermanagement.exception.service.UserAlreadyExistsException;
import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.request.UserCreateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.request.UserUpdateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static com.alexvait.accountingapi.helpers.UserTestObjectGenerator.createTestUserDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@DisplayName("Test User controller with mockMvc")
@ActiveProfiles("testing")
class UserControllerSpringMVCTestIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper jsonMapper = new ObjectMapper();

    @Test
    @DisplayName("Test get user")
    void testGetUser() throws Exception {
        // arrange
        UserDto userDto = createTestUserDto();
        when(userService.getUserByPublicId(anyString())).thenReturn(userDto);

        // act
        mockMvc.perform(get(UserController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.publicId").value(userDto.getPublicId()))
                .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userDto.getLastName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()));

        // assert
        verify(userService, times(1)).getUserByPublicId(anyString());
    }

    @Test
    @DisplayName("Test get user not found")
    void testGetUserNotFound() throws Exception {
        // arrange
        UserDto userDto = createTestUserDto();
        when(userService.getUserByPublicId(anyString())).thenThrow(new UsernameNotFoundException(""));

        // act
        mockMvc.perform(get(UserController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details.status").value("400"))
                .andExpect(jsonPath("$.responseState").value("FAILURE"))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"));

        // assert
        verify(userService, times(1)).getUserByPublicId(anyString());
    }

    @Test
    @DisplayName("Test create user")
    void testCreateUser() throws Exception {
        // arrange
        UserDto userDto = createTestUserDto();
        UserCreateRequestModel createRequestModel = UserMapper.INSTANCE.userDtoToUserCreateRequestModel(userDto);
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        // act, assert
        MvcResult result = mockMvc.perform(post(UserController.BASE_URL)
                .content(jsonMapper.writeValueAsString(createRequestModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.publicId").value(userDto.getPublicId()))
                .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userDto.getLastName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$._links.self").isMap())
                .andReturn();

        EntityModel<UserResponseModel> returnedEntity = jsonMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                });
        UserResponseModel createdUserModel = returnedEntity.getContent();

        assertThat(createdUserModel, equalTo(UserMapper.INSTANCE.userDtoToResponseModel(userDto)));
        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    @DisplayName("Test create user with already registered email")
    void testCreateUserEmailExists() throws Exception {
        // arrange
        UserDto userDto = createTestUserDto();
        UserCreateRequestModel createRequestModel = UserMapper.INSTANCE.userDtoToUserCreateRequestModel(userDto);
        when(userService.createUser(any(UserDto.class))).thenThrow(new UserAlreadyExistsException(""));

        // act
        mockMvc.perform(post(UserController.BASE_URL)
                .content(jsonMapper.writeValueAsString(createRequestModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details.status").value("500"))
                .andExpect(jsonPath("$.responseState").value("FAILURE"))
                .andExpect(jsonPath("$.httpStatus").value("INTERNAL_SERVER_ERROR"));

        // assert
        verify(userService, times(1)).createUser(any(UserDto.class));
    }

    @Test
    @DisplayName("Test update user")
    void testUpdateUser() throws Exception {
        // arrange
        UserDto userDto = createTestUserDto();
        UserUpdateRequestModel updateRequestModel = UserMapper.INSTANCE.userDtoToUserUpdateRequestModel(userDto);
        when(userService.updateUser(anyString(), any(UserDto.class))).thenReturn(userDto);

        // act, assert
        MvcResult result = mockMvc.perform(put(UserController.BASE_URL + "/x")
                .content(jsonMapper.writeValueAsString(updateRequestModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.publicId").value(userDto.getPublicId()))
                .andExpect(jsonPath("$.firstName").value(userDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userDto.getLastName()))
                .andExpect(jsonPath("$.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$._links.self").isMap())
                .andReturn();

        EntityModel<UserResponseModel> returnedEntity = jsonMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                });
        UserResponseModel updatedUserModel = returnedEntity.getContent();

        assertThat(updatedUserModel, equalTo(UserMapper.INSTANCE.userDtoToResponseModel(userDto)));
        verify(userService, times(1)).updateUser(anyString(), any(UserDto.class));
    }

    @Test
    @DisplayName("Test update user when user was not found")
    void testUpdateUserNotFound() throws Exception {
        // arrange
        UserDto userDto = createTestUserDto();
        UserUpdateRequestModel updateRequestModel = UserMapper.INSTANCE.userDtoToUserUpdateRequestModel(userDto);
        when(userService.updateUser(anyString(), any(UserDto.class))).thenThrow(new UsernameNotFoundException(""));

        // act, assert
        mockMvc.perform(put(UserController.BASE_URL + "/x")
                .content(jsonMapper.writeValueAsString(updateRequestModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details.status").value("400"))
                .andExpect(jsonPath("$.responseState").value("FAILURE"))
                .andExpect(jsonPath("$.httpStatus").value("BAD_REQUEST"));

        verify(userService, times(1)).updateUser(anyString(), any(UserDto.class));
    }

    @Test
    @DisplayName("Test delete user")
    void testDeleteUser() throws Exception {
        // arrange

        // act
        mockMvc.perform(delete(UserController.BASE_URL + "/1")
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
        mockMvc.perform(delete(UserController.BASE_URL + "/1")
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