package com.alexvait.accountingapi.helpers;

import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;

import java.security.SecureRandom;
import java.util.Random;

import static com.alexvait.accountingapi.security.utils.RandomStringUtils.randomAlphabetic;

public class UserTestObjectGenerator {

    private final static Random random = new SecureRandom();

    public static UserEntity createTestUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(random.nextInt(1000));
        userEntity.setPublicId(randomAlphabetic(40));
        userEntity.setFirstName(randomAlphabetic(20));
        userEntity.setLastName(randomAlphabetic(20));
        userEntity.setEmail(randomAlphabetic(20) + "@api.com");
        userEntity.setEncryptedPassword(randomAlphabetic(40));
        return userEntity;
    }

    public static UserDto createTestUserDto() {
        UserDto userDto = UserMapper.INSTANCE.userEntityToDto(UserTestObjectGenerator.createTestUserEntity());
        userDto.setPassword(randomAlphabetic(20));
        return userDto;
    }

}
