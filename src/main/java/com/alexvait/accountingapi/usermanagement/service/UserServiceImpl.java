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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
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
        UserEntity userEntity = userRepository.findByEmail(email);

        if (userEntity == null)
            throw new UsernameNotFoundException(email);

        return userMapper.userEntityToDto(userEntity);
    }

    @Override
    public UserDto getUserByPublicId(String publicId) {
        UserEntity userEntity = userRepository.findByPublicId(publicId);

        if (null == userEntity)
            throw new UsernameNotFoundException("No user found with public id " + publicId);

        return userMapper.userEntityToDto(userEntity);
    }

    @Override
    public UserDto updateUser(String publicId, UserDto userDto) {
        Objects.requireNonNull(userDto);

        UserEntity userEntity = userRepository.findByPublicId(publicId);

        if (userEntity == null)
            throw new UsernameNotFoundException("No user found with public id " + publicId);

        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());

        UserEntity updatedUserEntity = userRepository.save(userEntity);

        return userMapper.userEntityToDto(updatedUserEntity);
    }

    @Override
    public void deleteUserByPublicId(String publicId) {

        UserEntity userEntity = userRepository.findByPublicId(publicId);

        if (userEntity == null)
            throw new UsernameNotFoundException("No user found with public id " + publicId);

        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int size) {

        List<UserEntity> entitiesPage = userRepository.findAll(PageRequest.of(page, size)).getContent();

        return entitiesPage.stream().map(userMapper::userEntityToDto).collect(Collectors.toList());
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

        return userMapper.userEntityToDto(userRepository.save(userEntity));
    }

    private Collection<RoleEntity> getRoleEntitiesFromRoleNames(Collection<String> roleNames) {
        return roleNames.stream()
                .map(roleRepository::findByName)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
