package com.alexvait.accountingapi.usermanagement.mapper;

import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.request.UserCreateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.request.UserUpdateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userCreateRequestModelToDto(UserCreateRequestModel userCreateRequestModel);

    UserDto userUpdateRequestModelToDto(UserUpdateRequestModel userUpdateRequestModel);

//    @Mapping(target = "roles", ignore = true)
    UserDto userEntityToDto(UserEntity userEntity);

//    @Mapping(target = "roles", ignore = true)
    UserEntity userDtoToEntity(UserDto userDto);

    UserResponseModel userDtoToResponseModel(UserDto userDto);

    UserCreateRequestModel userDtoToUserCreateRequestModel(UserDto userDto);

    UserUpdateRequestModel userDtoToUserUpdateRequestModel(UserDto userDto);
}
