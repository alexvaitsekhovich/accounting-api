package com.alexvait.accountingapi.usermanagement.service;

import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUser(String email);
    UserDto getUserByPublicId(String publicId);
    UserDto updateUser(String publicId, UserDto userDto);
    void deleteUserByPublicId(String publicId);
    List<UserDto> getUsers(int page, int size);
}
