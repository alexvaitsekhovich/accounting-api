package com.alexvait.accountingapi.security.service;

import com.alexvait.accountingapi.security.utils.RandomStringUtils;
import com.alexvait.accountingapi.security.entity.UserEntity;
import com.alexvait.accountingapi.security.exception.service.UserAlreadyExistsException;
import com.alexvait.accountingapi.security.model.dto.UserDto;
import com.alexvait.accountingapi.security.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Objects.requireNonNull(userDto);

        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new UserAlreadyExistsException(String.format("User with email %s is already registered", userDto.getEmail()));
        }

        UserEntity userEntity = new UserEntity();

        BeanUtils.copyProperties(userDto, userEntity);

        userEntity.setPublicId(RandomStringUtils.randomAlphanumeric(40));
        userEntity.setEncryptedPassword(passwordEncoder.encode(userDto.getPassword()));

        UserEntity savedUser = userRepository.save(userEntity);
        UserDto savedUserDto = new UserDto();
        BeanUtils.copyProperties(savedUser, savedUserDto);

        return savedUserDto;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (null == userEntity)
            throw new UsernameNotFoundException(email);

        UserDto userDto = new UserDto();

        BeanUtils.copyProperties(userEntity, userDto);
        return userDto;
    }

    @Override
    public UserDto getUserByPublicId(String publicId) {
        UserEntity userEntity = userRepository.findByPublicId(publicId);

        if (null == userEntity)
            throw new UsernameNotFoundException("No user found with public id " + publicId);

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userEntity, userDto);

        return userDto;
    }

    @Override
    public UserDto updateUser(String publicId, UserDto userDto) {
        UserEntity userEntity = userRepository.findByPublicId(publicId);

        if (null == userEntity)
            throw new UsernameNotFoundException("No user found with public id " + publicId);

        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());

        UserEntity updatedUserEntity = userRepository.save(userEntity);
        UserDto updatedUserDto = new UserDto();
        BeanUtils.copyProperties(updatedUserEntity, updatedUserDto);

        return updatedUserDto;
    }

    @Override
    public void deleteUserByPublicId(String publicId) {
        UserEntity userEntity = userRepository.findByPublicId(publicId);

        if (null == userEntity)
            throw new UsernameNotFoundException("No user found with public id " + publicId);

        userRepository.delete(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (null == userEntity)
            throw new UsernameNotFoundException(email);

        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}
