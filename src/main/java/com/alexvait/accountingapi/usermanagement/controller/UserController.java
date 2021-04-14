package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.usermanagement.controller.annotated.UserControllerAnnotated;
import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.request.UserCreateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.request.UserUpdateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.response.UsersHateoasBuilderUtil;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(UserController.BASE_URL)
@CrossOrigin(origins = "*")
public class UserController implements UserControllerAnnotated {

    public static final String BASE_URL = "/user";

    private final UserService userService;
    private final UserMapper userMapper = UserMapper.INSTANCE;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserResponseModel> createUser(@Valid @RequestBody UserCreateRequestModel userReqModel) {

        UserDto newUser = userMapper.userCreateRequestModelToDto(userReqModel);
        UserDto createdUserDto = userService.createUser(newUser);

        return getUserResponseModelHateoasFromDto(createdUserDto);
    }

    @Override
    @GetMapping("/{publicId}")
    public EntityModel<UserResponseModel> getUser(@PathVariable String publicId) {

        UserDto userDto = userService.getUserByPublicId(publicId);
        return getUserResponseModelHateoasFromDto(userDto);
    }

    @Override
    @PutMapping("/{publicId}")
    public EntityModel<UserResponseModel> updateUser(@PathVariable String publicId,
                                                     @RequestBody UserUpdateRequestModel userReqModel) {

        UserDto userDto = userMapper.userUpdateRequestModelToDto(userReqModel);

        UserDto createdUserDto = userService.updateUser(publicId, userDto);

        return getUserResponseModelHateoasFromDto(createdUserDto);
    }

    private EntityModel<UserResponseModel> getUserResponseModelHateoasFromDto(UserDto userDto) {
        return UsersHateoasBuilderUtil.getUserResponseModelHateoasFromDto(userDto);
    }

}
