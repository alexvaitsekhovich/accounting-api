package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.security.config.SecurityConstants;
import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.request.UserCreateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.request.UserUpdateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.response.HateoasBuilderUtil;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserResponseModel> createUser(@RequestBody UserCreateRequestModel userReqModel) {

        UserDto newUser = userMapper.userCreateRequestModelToDto(userReqModel);
        newUser.setRoles(Collections.singletonList(SecurityConstants.ROLE_USER));
        UserDto createdUserDto = userService.createUser(newUser);

        return HateoasBuilderUtil.getUserResponseModelHateoasFromDto(createdUserDto);
    }

    @GetMapping("/{publicId}")
    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_ADMIN + "') || #publicId == principal.publicId")
    public EntityModel<UserResponseModel> getUser(@PathVariable String publicId) {

        UserDto userDto = userService.getUserByPublicId(publicId);
        return HateoasBuilderUtil.getUserResponseModelHateoasFromDto(userDto);
    }

    @PutMapping("/{publicId}")
    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_ADMIN + "') || #publicId == principal.publicId")
    public EntityModel<UserResponseModel> updateUser(@PathVariable String publicId, @RequestBody UserUpdateRequestModel userReqModel) {

        UserDto userDto = userMapper.userUpdateRequestModelToDto(userReqModel);

        UserDto createdUserDto = userService.updateUser(publicId, userDto);

        return HateoasBuilderUtil.getUserResponseModelHateoasFromDto(createdUserDto);
    }
}
