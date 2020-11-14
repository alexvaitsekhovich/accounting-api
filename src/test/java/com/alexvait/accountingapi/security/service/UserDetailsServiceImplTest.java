package com.alexvait.accountingapi.security.service;

import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import com.alexvait.accountingapi.usermanagement.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static com.alexvait.accountingapi.helpers.UserTestObjectGenerator.createTestUserEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Test UserDetailsService implementation")
class UserDetailsServiceImplTest {

    @InjectMocks
    UserDetailsServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Test
    @DisplayName("Test loadUserByUsername")
    void loadUserByUsername() {
        // arrange
        UserEntity userEntity = createTestUserEntity();
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        // act
        UserDetails userDetails = userService.loadUserByUsername("test");

        // assert
        verify(userRepository, times(1)).findByEmail(anyString());
        assertEquals(userEntity.getEmail(), userDetails.getUsername());
        assertEquals(userEntity.getEncryptedPassword(), userDetails.getPassword());
        assertEquals(0, userDetails.getAuthorities().size());
    }

    @Test
    @DisplayName("Test loadUserByUsername when user not found")
    void loadUserByUsernameNotFound() {
        // arrange
        when(userRepository.findByEmail(anyString())).thenReturn(null);

        // act, assert
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("test"));
        verify(userRepository, times(1)).findByEmail(anyString());
    }
}