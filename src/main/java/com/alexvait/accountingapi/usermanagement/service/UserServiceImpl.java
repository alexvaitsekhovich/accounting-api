package com.alexvait.accountingapi.usermanagement.service;

import com.alexvait.accountingapi.security.config.SecurityConstants;
import com.alexvait.accountingapi.security.entity.RoleEntity;
import com.alexvait.accountingapi.security.repository.RoleRepository;
import com.alexvait.accountingapi.security.utils.RandomStringUtils;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import com.alexvait.accountingapi.usermanagement.exception.service.UserAlreadyExistsException;
import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = UserMapper.INSTANCE;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        return createUserWithRoles(userDto, Collections.singletonList(SecurityConstants.ROLE_USER));
    }

    @Override
    public UserDto getUser(String email) {
        return convertUserEntityToDto(findUserByEmail(email));
    }

    @Override
    public UserDto getUserByPublicId(String publicId) {
        return convertUserEntityToDto(findUserByPublicId(publicId));
    }

    @Override
    public UserDto updateUser(String publicId, UserDto userDto) {
        Objects.requireNonNull(userDto);

        UserEntity userEntity = findUserByPublicId(publicId);

        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());

        return convertUserEntityToDto(userRepository.save(userEntity));
    }

    @Override
    public void deleteUserByPublicId(String publicId) {
        userRepository.delete(findUserByPublicId(publicId));
    }

    @Override
    public List<UserDto> getUsers(int page, int size) {

        List<UserEntity> entitiesPage = userRepository.findAll(PageRequest.of(page, size)).getContent();

        return entitiesPage.stream()
                .map(userMapper::userEntityToDto)
                .collect(Collectors.toList());
    }

    protected UserDto createUserWithRoles(UserDto userDto, Collection<String> roleNames) {
        Objects.requireNonNull(userDto);

        if (userRepository.findByEmail(userDto.getEmail()) != null) {
            throw new UserAlreadyExistsException(String.format("User with email %s is already registered", userDto.getEmail()));
        }

        UserEntity userEntity = userMapper.userDtoToEntity(userDto);

        userEntity.setEncryptedPassword(passwordEncoder.encode(userDto.getPassword()));
        userEntity.setPublicId(RandomStringUtils.randomAlphanumeric(40));
        userEntity.setRoles(getRoleEntitiesFromRoleNames(roleNames));

        return convertUserEntityToDto(userRepository.save(userEntity));
    }

    private Set<RoleEntity> getRoleEntitiesFromRoleNames(Collection<String> roleNames) {
        return roleNames.stream()
                .map(roleRepository::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private UserDto convertUserEntityToDto(UserEntity userEntity) {
        return userMapper.userEntityToDto(userEntity);
    }

    private UserEntity findUserByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        return userEntity;
    }

    private UserEntity findUserByPublicId(String publicId) {
        UserEntity userEntity = userRepository.findByPublicId(publicId);

        if (userEntity == null)
            throw new UsernameNotFoundException("No user found with public id: " + publicId);

        return userEntity;
    }
}
