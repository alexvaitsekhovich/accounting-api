package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.request.UserCreateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.request.UserUpdateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.alexvait.accountingapi.helpers.UserTestObjectGenerator.createTestUserDto;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test User controller")
class UserControllerTest {

    @InjectMocks
    UserController userController;

    @Mock
    UserService userService;

    UserMapper userMapper = UserMapper.INSTANCE;

    @Test
    @DisplayName("Test get user")
    void testGetUser() {
        // arrange
        UserDto userDto = createTestUserDto();
        when(userService.getUserByPublicId(anyString())).thenReturn(userDto);

        // act
        EntityModel<UserResponseModel> userEntityModel = userController.getUser("1");

        //assert
        assertEquals(userMapper.userDtoToResponseModel(userDto), userEntityModel.getContent());
        verify(userService, times(1)).getUserByPublicId(anyString());
    }

    @Test
    @DisplayName("Test get user not found")
    void testGetUserNotFound() {
        // arrange
        when(userService.getUserByPublicId(anyString())).thenThrow(new UsernameNotFoundException(""));

        // act, assert
        assertThrows(UsernameNotFoundException.class, () -> userController.getUser("1"));
        verify(userService, times(1)).getUserByPublicId(anyString());
    }


    @Test
    @DisplayName("Test create user")
    void testCreateUser() {
        // arrange
        UserDto userDto = createTestUserDto();
        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        // act
        EntityModel<UserResponseModel> userEntityModel = userController.createUser(new UserCreateRequestModel());

        //assert
        assertEquals(userMapper.userDtoToResponseModel(userDto), userEntityModel.getContent());
        verify(userService, times(1)).createUser(any());
    }

    @Test
    @DisplayName("Test update user")
    void testUpdateUser() {
        // arrange
        UserDto userDto = createTestUserDto();
        when(userService.updateUser(anyString(), any(UserDto.class))).thenReturn(userDto);

        // act
        EntityModel<UserResponseModel> userEntityModel = userController.updateUser("x", new UserUpdateRequestModel());

        //assert
        assertEquals(userMapper.userDtoToResponseModel(userDto), userEntityModel.getContent());
        verify(userService, times(1)).updateUser(anyString(), any());
    }
}