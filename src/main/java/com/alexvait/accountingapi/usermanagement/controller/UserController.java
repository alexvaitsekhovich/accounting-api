package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.usermanagement.exception.OperationResponse;
import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.request.UserCreateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.request.UserUpdateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.response.HateoasBuilder;
import com.alexvait.accountingapi.usermanagement.model.response.ResponseOperationState;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UserController.BASE_URL)
@CrossOrigin(origins = "*")
public class UserController {

    public static final String BASE_URL = "/user";

    private final UserService userService;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{publicId}")
    public EntityModel<UserResponseModel> getUser(@PathVariable String publicId) {

        UserDto userDto = userService.getUserByPublicId(publicId);
        return HateoasBuilder.getUserResponseModelHateoasFromDto(userDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserResponseModel> createUser(@RequestBody UserCreateRequestModel userReqModel) {

        UserDto createdUserDto = userService.createUser(userMapper.userCreateRequestModelToDto(userReqModel));

        return HateoasBuilder.getUserResponseModelHateoasFromDto(createdUserDto);
    }

    @PutMapping("/{publicId}")
    public EntityModel<UserResponseModel> updateUser(@PathVariable String publicId, @RequestBody UserUpdateRequestModel userReqModel) {

        UserDto userDto = userMapper.userUpdateRequestModelToDto(userReqModel);

        UserDto createdUserDto = userService.updateUser(publicId, userDto);

        return HateoasBuilder.getUserResponseModelHateoasFromDto(createdUserDto);
    }

    @DeleteMapping("/{publicId}")
    public OperationResponse deleteUser(@PathVariable String publicId) {

        userService.deleteUserByPublicId(publicId);
        OperationResponse operationResponse = new OperationResponse(ResponseOperationState.SUCCESS, HttpStatus.OK);

        return operationResponse;
    }
}
