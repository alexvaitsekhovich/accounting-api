package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.request.RequestOperations;
import com.alexvait.accountingapi.usermanagement.model.request.UserCreateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.request.UserUpdateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.response.OperationResponseModel;
import com.alexvait.accountingapi.usermanagement.model.response.ResponseOperationStates;
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

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Test get user")
    void getUser() {
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
    @DisplayName("Test create user")
    void createUser() {
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
    void updateUser() {
        // arrange
        UserDto userDto = createTestUserDto();
        when(userService.updateUser(anyString(), any(UserDto.class))).thenReturn(userDto);

        // act
        EntityModel<UserResponseModel> userEntityModel = userController.updateUser("x", new UserUpdateRequestModel());

        //assert
        assertEquals(userMapper.userDtoToResponseModel(userDto), userEntityModel.getContent());
        verify(userService, times(1)).updateUser(anyString(), any());
    }

    @Test
    @DisplayName("Test delete user")
    void deleteUser() {
        // arrange

        // act
        OperationResponseModel operationResponse = userController.deleteUser("x");

        //assert
        verify(userService, times(1)).deleteUserByPublicId(anyString());
        assertEquals(RequestOperations.DELETE, operationResponse.getOperation());
        assertEquals(ResponseOperationStates.SUCCESS, operationResponse.getResult());
    }

    @Test
    @DisplayName("Test delete user when no user found")
    void deleteUserNotFound() {
        // arrange
        doThrow(new UsernameNotFoundException("")).when(userService).deleteUserByPublicId(anyString());

        // act
        assertThrows(UsernameNotFoundException.class, () -> userController.deleteUser("x"));

        //assert
        verify(userService, times(1)).deleteUserByPublicId(anyString());
    }

}