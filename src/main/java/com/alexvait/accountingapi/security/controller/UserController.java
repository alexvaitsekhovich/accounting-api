package com.alexvait.accountingapi.security.controller;

import com.alexvait.accountingapi.security.model.dto.UserDto;
import com.alexvait.accountingapi.security.model.request.RequestOperations;
import com.alexvait.accountingapi.security.model.request.UserCreateRequestModel;
import com.alexvait.accountingapi.security.model.request.UserUpdateRequestModel;
import com.alexvait.accountingapi.security.model.response.OperationResponseModel;
import com.alexvait.accountingapi.security.model.response.ResponseOperationStates;
import com.alexvait.accountingapi.security.model.response.UserResponseModel;
import com.alexvait.accountingapi.security.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(UserController.BASE_URL)
public class UserController {

    public static final String BASE_URL = "/user";

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{publicId}")
    public UserResponseModel getUser(@PathVariable String publicId) {
        UserResponseModel user = new UserResponseModel();

        UserDto userDto = userService.getUserByPublicId(publicId);
        BeanUtils.copyProperties(userDto, user);

        return user;
    }

    @PostMapping
    public UserResponseModel createUser(@RequestBody UserCreateRequestModel userReqModel) {

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userReqModel, userDto);

        UserDto createdUserDto = userService.createUser(userDto);

        UserResponseModel createdUserModel = new UserResponseModel();
        BeanUtils.copyProperties(createdUserDto, createdUserModel);

        return createdUserModel;
    }

    @PutMapping("/{publicId}")
    public UserResponseModel updateUser(@PathVariable String publicId, @RequestBody UserUpdateRequestModel userReqModel) {
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userReqModel, userDto);

        UserDto createdUserDto = userService.updateUser(publicId, userDto);

        UserResponseModel updatedUserModel = new UserResponseModel();
        BeanUtils.copyProperties(createdUserDto, updatedUserModel);

        return updatedUserModel;
    }

    @DeleteMapping("/{publicId}")
    public OperationResponseModel deleteUser(@PathVariable String publicId) {

        OperationResponseModel operationResponse = new OperationResponseModel(RequestOperations.DELETE);

        userService.deleteUserByPublicId(publicId);
        operationResponse.setResult(ResponseOperationStates.SUCCESS);

        return operationResponse;
    }
}
