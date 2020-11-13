package com.alexvait.accountingapi.helpers;

import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;

import java.util.Random;

import static com.alexvait.accountingapi.security.utils.RandomStringUtils.randomAlphabetic;

public class UserTestObjectGenerator {

    public static UserEntity createTestUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(new Random().nextInt(1000));
        userEntity.setPublicId(randomAlphabetic(100));
        userEntity.setFirstName(randomAlphabetic(100));
        userEntity.setLastName(randomAlphabetic(100));
        userEntity.setEmail(randomAlphabetic(100));
        userEntity.setEncryptedPassword(randomAlphabetic(100));
        return userEntity;
    }

    public static UserDto createTestUserDto() {
        UserDto userDto = UserMapper.INSTANCE.userEntityToDto(UserTestObjectGenerator.createTestUserEntity());
        userDto.setPassword(randomAlphabetic(100));
        return userDto;
    }

}
