package com.alexvait.accountingapi.security.service;

import com.alexvait.accountingapi.security.model.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUser(String email);
    UserDto getUserByPublicId(String publicId);
    UserDto updateUser(String publicId, UserDto userDto);
    void deleteUserByPublicId(String publicId);
}
