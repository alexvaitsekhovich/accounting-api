package com.alexvait.accountingapi.usermanagement.service;

import com.alexvait.accountingapi.security.repository.RoleRepository;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import com.alexvait.accountingapi.usermanagement.exception.service.UserAlreadyExistsException;
import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

import static com.alexvait.accountingapi.helpers.UserTestObjectGenerator.createTestUserDto;
import static com.alexvait.accountingapi.helpers.UserTestObjectGenerator.createTestUserEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test User service implementation")
class UserServiceImplTest {

    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Test createUser")
    void createUser() {
        // arrange
        UserDto newUserDto = createTestUserDto();
        // make sure these fields are not set
        newUserDto.setPublicId(null);
        newUserDto.setEncryptedPassword(null);

        UserEntity createdUserEntity = createTestUserEntity();

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(passwordEncoder.encode(anyString())).thenReturn("1234");

        final ArgumentCaptor<UserEntity> entityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.save(entityArgumentCaptor.capture())).thenReturn(createdUserEntity);

        // act
        UserDto createdUserDto = userService.createUser(newUserDto);

        // assert
        assertAll(
                () -> assertNotNull(createdUserDto, "not null failed"),
                () -> assertEquals(UserMapper.INSTANCE.userEntityToDto(createdUserEntity), createdUserDto, "equals to entity failed")
        );

        UserEntity createUserEntityCaptor = entityArgumentCaptor.getValue();

        assertAll(
                "test fields of the created user dto",

                // these fields should be new
                () -> assertNotEquals(newUserDto.getPublicId(), createUserEntityCaptor.getPublicId(), "public id comparison failed"),
                () -> assertNotEquals(newUserDto.getEncryptedPassword(), createUserEntityCaptor.getEncryptedPassword(), "encrypted password comparison failed"),

                // all other fields must stay the same
                () -> assertEquals(newUserDto.getFirstName(), createUserEntityCaptor.getFirstName(), "first name comparison failed"),
                () -> assertEquals(newUserDto.getLastName(), createUserEntityCaptor.getLastName(), "last name comparison failed"),
                () -> assertEquals(newUserDto.getEmail(), createUserEntityCaptor.getEmail(), "email comparison failed")
        );

        verify(userRepository).findByEmail(anyString());
        verify(userRepository).save(any(UserEntity.class));
        verify(passwordEncoder).encode(anyString());
    }

    @Test
    @DisplayName("Test createUser when the email is already registered")
    void createUserEmailExists() {
        // arrange
        UserEntity userEntity = createTestUserEntity();
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        // act, assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(createTestUserDto()));
        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Test getUser")
    void testGetUser() {
        // arrange
        UserEntity userEntity = createTestUserEntity();
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        // act
        UserDto userDto = userService.getUser("test");

        // assert
        assertNotNull(userDto);
        assertEquals(userEntity, UserMapper.INSTANCE.userDtoToEntity(userDto));
        verify(userRepository).findByEmail(anyString());
    }

    @Test
    @DisplayName("Test getUser when user not found")
    void testGetUserNotFound() {
        // arrange
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        // act, assert
        assertThrows(UsernameNotFoundException.class, () -> userService.getUser("test"));
        verify(userRepository).findByEmail(anyString());
    }

    @Test
    @DisplayName("Test getUserByPublicId")
    void testGetUserByPublicId() {
        // arrange
        UserEntity userEntity = createTestUserEntity();
        when(userRepository.findByPublicId(anyString())).thenReturn(userEntity);

        // act
        UserDto userDto = userService.getUserByPublicId("test");

        // assert
        assertNotNull(userDto);
        assertEquals(userEntity, UserMapper.INSTANCE.userDtoToEntity(userDto));
        verify(userRepository).findByPublicId(anyString());
    }

    @Test
    @DisplayName("Test getUserByPublicId when user not found")
    void testGetUserByPublicIdNotFound() {
        // arrange
        when(userRepository.findByPublicId(anyString())).thenReturn(null);

        // act, assert
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByPublicId("test"));
        verify(userRepository).findByPublicId(anyString());
    }

    @Test
    @DisplayName("Test updateUser changes only certain fields")
    void testUpdateUserSettingData() {
        // arrange
        UserEntity originalUserEntity = createTestUserEntity();
        when(userRepository.findByPublicId(anyString())).thenReturn(originalUserEntity);

        final ArgumentCaptor<UserEntity> entityArgumentCaptor = ArgumentCaptor.forClass(UserEntity.class);
        when(userRepository.save(entityArgumentCaptor.capture())).thenReturn(createTestUserEntity());

        // data to update the user
        UserDto userDto = createTestUserDto();

        // act
        UserDto createdUserDto = userService.updateUser(userDto.getPublicId(), userDto);

        // assert

        assertNotNull(createdUserDto);

        assertAll(
                "test fields of the updated user dto",

                // only first and last name must be changed to the provided data
                () -> assertEquals(userDto.getFirstName(), entityArgumentCaptor.getValue().getFirstName(), "first name comparison failed"),
                () -> assertEquals(userDto.getLastName(), entityArgumentCaptor.getValue().getLastName(), "last name comparison failed"),

                // all other fields must stay the same in the database
                () -> assertNotEquals(userDto.getId(), entityArgumentCaptor.getValue().getId(), "id comparison failed"),
                () -> assertNotEquals(userDto.getPublicId(), entityArgumentCaptor.getValue().getPublicId(), "public id comparison failed"),
                () -> assertNotEquals(userDto.getEmail(), entityArgumentCaptor.getValue().getEmail(), "email comparison failed"),
                () -> assertNotEquals(userDto.getEncryptedPassword(), entityArgumentCaptor.getValue().getEncryptedPassword(), "encrypted password comparison failed")
        );

        verify(userRepository).findByPublicId(anyString());
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Test updateUser converts data correctly")
    void testUpdateUserResult() {
        // arrange
        UserEntity updatedUserEntity = createTestUserEntity();

        when(userRepository.findByPublicId(anyString())).thenReturn(createTestUserEntity());
        when(userRepository.save(any())).thenReturn(updatedUserEntity);

        // placeholder
        UserDto userDto = createTestUserDto();

        // act
        UserDto updatedUserDto = userService.updateUser(userDto.getPublicId(), userDto);

        // assert
        assertNotNull(updatedUserDto);
        assertEquals(updatedUserDto, UserMapper.INSTANCE.userEntityToDto(updatedUserEntity));

        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Test updateUser when user not found")
    void testUpdateUserNotFound() {
        // arrange
        when(userRepository.findByPublicId(anyString())).thenReturn(null);
        // placeholder
        UserDto userDto = createTestUserDto();

        // act, assert
        assertThrows(UsernameNotFoundException.class, () -> userService.updateUser(userDto.getPublicId(), userDto));
        verify(userRepository).findByPublicId(anyString());
    }


    @Test
    @DisplayName("Test deleteUserByPublicId")
    void testDeleteUserByPublicId() {
        // arrange
        UserEntity userEntity = createTestUserEntity();
        when(userRepository.findByPublicId(anyString())).thenReturn(userEntity);

        // act
        userService.deleteUserByPublicId(userEntity.getPublicId());

        // assert
        verify(userRepository).delete(userEntity);
    }

    @Test
    @DisplayName("Test deleteUserByPublicId when user not found")
    void testDeleteUserByPublicIdNotFound() {
        // arrange
        when(userRepository.findByPublicId(anyString())).thenReturn(null);

        // act, assert
        assertThrows(UsernameNotFoundException.class, () -> userService.deleteUserByPublicId("x"));
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Test getUsers")
    void getUsers() {
        // arrange
        UserEntity userEntity1 = createTestUserEntity();
        UserEntity userEntity2 = createTestUserEntity();

        List<UserEntity> userEntities = List.of(userEntity1, userEntity2);
        PageRequest pageRequest = PageRequest.of(0, 2);

        when(userRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(userEntities));

        // act
        List<UserDto> usersDto = userService.getUsers(pageRequest.getPageNumber(), pageRequest.getPageSize());

        // assert
        assertNotNull(usersDto);

        List<UserDto> usersDtoExpected = List.of(UserMapper.INSTANCE.userEntityToDto(userEntity1), UserMapper.INSTANCE.userEntityToDto(userEntity2));
        assertEquals(usersDtoExpected, usersDto);
        assertThat(usersDtoExpected, containsInAnyOrder(usersDto.toArray()));
    }
}