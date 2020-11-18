package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.security.config.ControllerExceptionHandler;
import com.alexvait.accountingapi.usermanagement.exception.service.UserAlreadyExistsException;
import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.request.UserCreateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.request.UserUpdateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.alexvait.accountingapi.helpers.UserTestObjectGenerator.createTestUserDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test User controller with mockMvc")
@ActiveProfiles("testing")
class UserControllerSpringMVCTest {

    @Mock
    private UserService userService;

    private ObjectMapper jsonMapper;

    private MockMvc mockMvc;

    UserDto testingUserDto;

    @BeforeEach
    void setUp() {
        testingUserDto = createTestUserDto();
        jsonMapper = new ObjectMapper();

        UserController userController = new UserController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new ControllerExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Test get user")
    void testGetUser() throws Exception {
        // arrange
        when(userService.getUserByPublicId(anyString())).thenReturn(testingUserDto);

        // act
        mockMvc.perform(get(UserController.BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.publicId").value(testingUserDto.getPublicId()))
                .andExpect(jsonPath("$.firstName").value(testingUserDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testingUserDto.getLastName()))
                .andExpect(jsonPath("$.email").value(testingUserDto.getEmail()));

        // assert
        verify(userService).getUserByPublicId(anyString());
    }

    @Test
    @DisplayName("Test get user not found")
    void testGetUserNotFound() throws Exception {
        // arrange
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
        verify(userService).getUserByPublicId(anyString());
    }

    @Test
    @DisplayName("Test create user")
    void testCreateUser() throws Exception {
        // arrange
        UserCreateRequestModel createRequestModel = UserMapper.INSTANCE.userDtoToUserCreateRequestModel(testingUserDto);
        when(userService.createUser(any(UserDto.class))).thenReturn(testingUserDto);

        // act, assert
        MvcResult result = mockMvc.perform(post(UserController.BASE_URL)
                .content(jsonMapper.writeValueAsString(createRequestModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.publicId").value(testingUserDto.getPublicId()))
                .andExpect(jsonPath("$.firstName").value(testingUserDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testingUserDto.getLastName()))
                .andExpect(jsonPath("$.email").value(testingUserDto.getEmail()))
                .andReturn();

        EntityModel<UserResponseModel> returnedEntity = jsonMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                });
        UserResponseModel createdUserModel = returnedEntity.getContent();

        assertEquals(UserMapper.INSTANCE.userDtoToResponseModel(testingUserDto), createdUserModel, "Created user model comparison failed");
        verify(userService).createUser(any(UserDto.class));
    }

    @Test
    @DisplayName("Test create user with already registered email")
    void testCreateUserEmailExists() throws Exception {
        // arrange
        UserCreateRequestModel createRequestModel = UserMapper.INSTANCE.userDtoToUserCreateRequestModel(testingUserDto);
        when(userService.createUser(any(UserDto.class))).thenThrow(new UserAlreadyExistsException(""));

        // act
        mockMvc.perform(post(UserController.BASE_URL)
                .content(jsonMapper.writeValueAsString(createRequestModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.details.status").value("409"))
                .andExpect(jsonPath("$.responseState").value("FAILURE"))
                .andExpect(jsonPath("$.httpStatus").value("CONFLICT"));

        // assert
        verify(userService).createUser(any(UserDto.class));
    }

    @Test
    @DisplayName("Test update user")
    void testUpdateUser() throws Exception {
        // arrange
        UserUpdateRequestModel updateRequestModel = UserMapper.INSTANCE.userDtoToUserUpdateRequestModel(testingUserDto);
        when(userService.updateUser(anyString(), any(UserDto.class))).thenReturn(testingUserDto);

        // act, assert
        MvcResult result = mockMvc.perform(put(UserController.BASE_URL + "/x")
                .content(jsonMapper.writeValueAsString(updateRequestModel))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.publicId").value(testingUserDto.getPublicId()))
                .andExpect(jsonPath("$.firstName").value(testingUserDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testingUserDto.getLastName()))
                .andExpect(jsonPath("$.email").value(testingUserDto.getEmail()))
                .andReturn();

        EntityModel<UserResponseModel> returnedEntity = jsonMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {
                });
        UserResponseModel updatedUserModel = returnedEntity.getContent();

        assertEquals(UserMapper.INSTANCE.userDtoToResponseModel(testingUserDto), updatedUserModel, "Updated model comparison failed");
        verify(userService).updateUser(anyString(), any(UserDto.class));
    }

    @Test
    @DisplayName("Test update user when user was not found")
    void testUpdateUserNotFound() throws Exception {
        // arrange
        UserUpdateRequestModel updateRequestModel = UserMapper.INSTANCE.userDtoToUserUpdateRequestModel(testingUserDto);
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

        verify(userService).updateUser(anyString(), any(UserDto.class));
    }
}