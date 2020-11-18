package com.alexvait.accountingapi.usermanagement.service;

import com.alexvait.accountingapi.security.entity.RoleEntity;
import com.alexvait.accountingapi.security.repository.RoleRepository;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import com.alexvait.accountingapi.usermanagement.exception.service.UserAlreadyExistsException;
import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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

    UserEntity testingUserEntity;
    UserDto testingUserDto;

    @BeforeEach
    void setUp() {
        testingUserEntity = createTestUserEntity();
        testingUserDto = createTestUserDto();
    }

    @Test
    @DisplayName("Test createUser when the email is already registered")
    void createUserEmailExists() {
        // arrange
        when(userRepository.findByEmail(anyString())).thenReturn(testingUserEntity);

        // act, assert
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(testingUserDto));
        verify(userRepository, never()).save(any(UserEntity.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Test getUser")
    void testGetUser() {
        // arrange
        when(userRepository.findByEmail(anyString())).thenReturn(testingUserEntity);

        // act
        UserDto userDto = userService.getUser("test@api.com");

        // assert
        assertNotNull(userDto);
        assertEquals(testingUserEntity, UserMapper.INSTANCE.userDtoToEntity(userDto));
        verify(userRepository).findByEmail(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Test getUser when user not found")
    void testGetUserNotFound() {
        // arrange
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        // act, assert
        assertThrows(UsernameNotFoundException.class, () -> userService.getUser("test@api.com"));
        verify(userRepository).findByEmail(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Test getUserByPublicId")
    void testGetUserByPublicId() {
        // arrange
        when(userRepository.findByPublicId(anyString())).thenReturn(testingUserEntity);

        // act
        UserDto userDto = userService.getUserByPublicId("test");

        // assert
        assertNotNull(userDto);
        assertEquals(testingUserEntity, UserMapper.INSTANCE.userDtoToEntity(userDto));
        verify(userRepository).findByPublicId(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Test getUserByPublicId when user not found")
    void testGetUserByPublicIdNotFound() {
        // arrange
        when(userRepository.findByPublicId(anyString())).thenReturn(null);

        // act, assert
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByPublicId("test"));
        verify(userRepository).findByPublicId(anyString());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Test updateUser converts data correctly")
    void testUpdateUserResult() {
        // arrange
        when(userRepository.findByPublicId(anyString())).thenReturn(createTestUserEntity());
        when(userRepository.save(any())).thenReturn(testingUserEntity);

        // act
        UserDto updatedUserDto = userService.updateUser("X", testingUserDto);

        // assert
        assertNotNull(updatedUserDto);
        assertEquals(updatedUserDto, UserMapper.INSTANCE.userEntityToDto(testingUserEntity));

        verify(userRepository).save(any(UserEntity.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Test updateUser when user not found")
    void testUpdateUserNotFound() {
        // arrange
        when(userRepository.findByPublicId(anyString())).thenReturn(null);

        // act, assert
        assertThrows(UsernameNotFoundException.class, () -> userService.updateUser("X", testingUserDto));
        verify(userRepository).findByPublicId(anyString());
        verifyNoMoreInteractions(userRepository);
    }


    @Test
    @DisplayName("Test deleteUserByPublicId")
    void testDeleteUserByPublicId() {
        // arrange
        when(userRepository.findByPublicId(anyString())).thenReturn(testingUserEntity);

        // act
        userService.deleteUserByPublicId(testingUserEntity.getPublicId());

        // assert
        verify(userRepository).delete(testingUserEntity);
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    @DisplayName("Test deleteUserByPublicId when user not found")
    void testDeleteUserByPublicIdNotFound() {
        // arrange
        when(userRepository.findByPublicId(anyString())).thenReturn(null);

        // act, assert
        assertThrows(UsernameNotFoundException.class, () -> userService.deleteUserByPublicId("test"));
        verify(userRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Test getUsers")
    void getUsers() {
        // arrange
        UserEntity userEntity2 = createTestUserEntity();

        List<UserEntity> userEntities = List.of(testingUserEntity, userEntity2);
        PageRequest pageRequest = PageRequest.of(0, 2);

        when(userRepository.findAll(any(PageRequest.class))).thenReturn(new PageImpl<>(userEntities));

        // act
        List<UserDto> usersDto = userService.getUsers(pageRequest.getPageNumber(), pageRequest.getPageSize());

        // assert
        assertNotNull(usersDto);

        List<UserDto> usersDtoExpected = List.of(
                UserMapper.INSTANCE.userEntityToDto(testingUserEntity),
                UserMapper.INSTANCE.userEntityToDto(userEntity2)
        );
        assertEquals(usersDtoExpected, usersDto);
        assertThat(usersDtoExpected, containsInAnyOrder(usersDto.toArray()));

        verify(userRepository).findAll(any(PageRequest.class));
        verifyNoMoreInteractions(userRepository);
    }

    @Nested
    @DisplayName("Test User service implementation with UserEntity captor")
    class UserServiceImplWithCaptorTest {

        @Captor
        ArgumentCaptor<UserEntity> entityArgumentCaptor;

        @Test
        @DisplayName("Test createUser")
        void createUser() {
            // arrange
            // make sure these fields are not set
            testingUserDto.setPublicId(null);
            testingUserDto.setEncryptedPassword(null);

            List<String> roles = Arrays.asList("ROLE_ADMIN", "ROLE_USER", "ROLE_SOMEBODY");
            testingUserDto.setRoles(roles);

            when(userRepository.findByEmail(anyString())).thenReturn(null);
            when(userRepository.save(entityArgumentCaptor.capture())).thenReturn(testingUserEntity);
            when(passwordEncoder.encode(anyString())).thenReturn("1234");
            when(roleRepository.findByName(anyString())).thenReturn(
                    new RoleEntity(UUID.randomUUID().toString()),
                    new RoleEntity(UUID.randomUUID().toString()),
                    new RoleEntity(UUID.randomUUID().toString())
            );

            InOrder inOrder = inOrder(userRepository, passwordEncoder, roleRepository);

            // act
            UserDto createdUserDto = userService.createUser(testingUserDto);

            // assert
            assertAll(
                    () -> assertNotNull(createdUserDto, "not null failed"),
                    () -> assertEquals(UserMapper.INSTANCE.userEntityToDto(testingUserEntity), createdUserDto, "equals to entity failed")
            );

            UserEntity createUserEntityCaptor = entityArgumentCaptor.getValue();

            assertAll(
                    "test fields of the created user dto",

                    () -> assertEquals(3, createUserEntityCaptor.getRoles().size()),

                    // these fields should be new
                    () -> assertNotEquals(testingUserDto.getPublicId(), createUserEntityCaptor.getPublicId(), "public id comparison failed"),
                    () -> assertNotEquals(testingUserDto.getEncryptedPassword(), createUserEntityCaptor.getEncryptedPassword(), "encrypted password comparison failed"),

                    // all other fields must stay the same
                    () -> assertEquals(testingUserDto.getFirstName(), createUserEntityCaptor.getFirstName(), "first name comparison failed"),
                    () -> assertEquals(testingUserDto.getLastName(), createUserEntityCaptor.getLastName(), "last name comparison failed"),
                    () -> assertEquals(testingUserDto.getEmail(), createUserEntityCaptor.getEmail(), "email comparison failed")
            );

            inOrder.verify(userRepository).findByEmail(anyString());
            inOrder.verify(passwordEncoder).encode(anyString());
            inOrder.verify(roleRepository, times(roles.size())).findByName(anyString());
            inOrder.verify(userRepository).save(any(UserEntity.class));
            verifyNoMoreInteractions(userRepository);
        }

        @Test
        @DisplayName("Test updateUser changes only certain fields")
        void testUpdateUserSettingData() {
            // arrange
            when(userRepository.findByPublicId(anyString())).thenReturn(testingUserEntity);
            when(userRepository.save(entityArgumentCaptor.capture())).thenReturn(createTestUserEntity());
            InOrder inOrder = inOrder(userRepository);

            // act
            UserDto createdUserDto = userService.updateUser("X", testingUserDto);

            // assert

            assertNotNull(createdUserDto);

            assertAll(
                    "test fields of the updated user dto",

                    // only first and last name must be changed to the provided data
                    () -> assertEquals(testingUserDto.getFirstName(), entityArgumentCaptor.getValue().getFirstName(), "first name comparison failed"),
                    () -> assertEquals(testingUserDto.getLastName(), entityArgumentCaptor.getValue().getLastName(), "last name comparison failed"),

                    // all other fields must stay the same in the database
                    () -> assertNotEquals(testingUserDto.getId(), entityArgumentCaptor.getValue().getId(), "id comparison failed"),
                    () -> assertNotEquals(testingUserDto.getPublicId(), entityArgumentCaptor.getValue().getPublicId(), "public id comparison failed"),
                    () -> assertNotEquals(testingUserDto.getEmail(), entityArgumentCaptor.getValue().getEmail(), "email comparison failed"),
                    () -> assertNotEquals(testingUserDto.getEncryptedPassword(), entityArgumentCaptor.getValue().getEncryptedPassword(), "encrypted password comparison failed")
            );

            inOrder.verify(userRepository).findByPublicId(anyString());
            inOrder.verify(userRepository).save(any(UserEntity.class));
            verifyNoMoreInteractions(userRepository);
        }
    }
}