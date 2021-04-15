package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.response.OperationResponse;
import com.alexvait.accountingapi.usermanagement.model.response.ResponseOperationState;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.alexvait.accountingapi.helpers.TestObjectsGenerator.createTestUserDto;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test Administration controller")
class AdminControllerTest {

    @InjectMocks
    AdminController adminController;

    @Mock
    UserService userService;

    @Test
    @DisplayName("Test getUsers")
    void testGetUsers() {
        // arrange
        UserDto userDto1 = createTestUserDto();
        UserDto userDto2 = createTestUserDto();

        List<UserDto> usersDto = List.of(userDto1, userDto2);
        when(userService.getUsers(anyInt(), anyInt())).thenReturn(usersDto);

        // act
        CollectionModel<EntityModel<UserResponseModel>> collectionModel = adminController.getUsers(0, 5);

        Collection<EntityModel<UserResponseModel>> responseModels = collectionModel.getContent();

        //assert

        assertAll(
                "test collection model",
                () -> assertTrue(collectionModel.getLinks().hasSize(1), "hasSize failed"),
                () -> assertNotNull(collectionModel.getLink("self"), "self link failed")
        );

        assertAll(
                "test entity models",
                () -> assertEquals(2, responseModels.size(), "size failed"),
                () -> assertThat(
                        "contains element failed",
                        usersDto.stream().map(UserMapper.INSTANCE::userDtoToResponseModel).collect(Collectors.toList())
                        , containsInAnyOrder(responseModels.stream().map(EntityModel::getContent).toArray())
                )
        );

        responseModels.forEach(responseModel ->
                assertAll(
                        "test entity models links for id #" + responseModel.getContent().getPublicId(),
                        () -> assertTrue(responseModel.getLinks().hasSize(2), "hasSize failed"),
                        () -> assertNotNull(responseModel.getLink("self"), "self link failed"),
                        () -> assertNotNull(responseModel.getLink("invoices"), "invoices link failed")
                )
        );

        verify(userService).getUsers(anyInt(), anyInt());
    }

    @Test
    @DisplayName("Test delete user")
    void testDeleteUser() {
        // arrange

        // act
        OperationResponse operationResponse = adminController.deleteUser("x");

        //assert
        assertAll(
                () -> assertEquals(HttpStatus.OK, operationResponse.getHttpStatus(), "status code failed"),
                () -> assertEquals(ResponseOperationState.SUCCESS, operationResponse.getResponseState(), "response failed")
        );

        verify(userService).deleteUserByPublicId(anyString());
    }

    @Test
    @DisplayName("Test delete user when no user was found")
    void testDeleteUserNotFound() {
        // arrange
        doThrow(new UsernameNotFoundException("")).when(userService).deleteUserByPublicId(anyString());

        // act
        assertThrows(UsernameNotFoundException.class, () -> adminController.deleteUser("x"));

        //assert
        verify(userService).deleteUserByPublicId(anyString());
    }
}