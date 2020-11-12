package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
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

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.alexvait.accountingapi.helpers.UserTestObjectGenerator.createTestUserDto;
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

        verify(userService, times(1)).getUsers(anyInt(), anyInt());

        // test collection model
        assertTrue(collectionModel.getLinks().hasSize(1));
        assertNotNull(collectionModel.getLink("self"));

        // test entity models
        assertEquals(2, responseModels.size());
        assertThat(
                usersDto.stream()
                        .map(UserMapper.INSTANCE::userDtoToResponseModel)
                        .collect(Collectors.toList())
                , containsInAnyOrder(
                        responseModels.stream().map(EntityModel::getContent).toArray()
                )
        );

        responseModels.forEach(responseModel -> {
            assertTrue(responseModel.getLinks().hasSize(2));
            assertNotNull(responseModel.getLink("self"));
            assertNotNull(responseModel.getLink("invoices"));
        });
    }
}