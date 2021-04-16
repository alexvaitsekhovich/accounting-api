package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.request.UserCreateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.request.UserUpdateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.alexvait.accountingapi.helpers.TestObjectsGenerator.createTestUserDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test User controller")
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    private final UserMapper userMapper = UserMapper.INSTANCE;

    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = createTestUserDto();
    }

    @Test
    @DisplayName("Test get user")
    void testGetUser() {
        // arrange
        when(userService.getUserByPublicId(anyString())).thenReturn(userDto);

        // act
        EntityModel<UserResponseModel> userEntityModel = userController.getUser("1");

        //assert
        assertEquals(userMapper.userDtoToResponseModel(userDto), userEntityModel.getContent());
        verify(userService).getUserByPublicId(anyString());
    }

    @Test
    @DisplayName("Test get user not found")
    void testGetUserNotFound() {
        // arrange
        when(userService.getUserByPublicId(anyString())).thenThrow(new UsernameNotFoundException(""));

        // act, assert
        assertThrows(UsernameNotFoundException.class, () -> userController.getUser("1"));
        verify(userService).getUserByPublicId(anyString());
    }


    @Test
    @DisplayName("Test create user")
    void testCreateUser() {
        // arrange
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        // act
        EntityModel<UserResponseModel> userEntityModel = userController.createUser(new UserCreateRequestModel());

        //assert
        assertEquals(userMapper.userDtoToResponseModel(userDto), userEntityModel.getContent());
        verify(userService).createUser(any());
    }

    @Test
    @DisplayName("Test update user")
    void testUpdateUser() {
        // arrange
        when(userService.updateUser(anyString(), any(UserDto.class))).thenReturn(userDto);

        // act
        EntityModel<UserResponseModel> userEntityModel = userController.updateUser("x", new UserUpdateRequestModel());

        //assert
        assertEquals(userMapper.userDtoToResponseModel(userDto), userEntityModel.getContent());
        verify(userService).updateUser(anyString(), any());
    }
}