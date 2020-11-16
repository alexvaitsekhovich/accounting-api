package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.security.config.SecurityConstants;
import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.request.UserCreateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.request.UserUpdateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.response.HateoasBuilderUtil;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    @Operation(summary = "Create a user", tags = "User management", description = "Use this endpoint to create a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User was created", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "409", description = "Email is already registered", content = {@Content(mediaType = "application/json")})}
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserResponseModel> createUser(
            @Parameter(description = "Data of the user to be created")
            @Valid @RequestBody UserCreateRequestModel userReqModel) {

        UserDto newUser = userMapper.userCreateRequestModelToDto(userReqModel);
        newUser.setRoles(Collections.singletonList(SecurityConstants.ROLE_USER));
        UserDto createdUserDto = userService.createUser(newUser);

        return HateoasBuilderUtil.getUserResponseModelHateoasFromDto(createdUserDto);
    }


    @Operation(summary = "Get user", tags = "User management", description = "Use this endpoint to get a user details",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "User was not found", content = {@Content(mediaType = "application/json")})}
    )
    @GetMapping("/{publicId}")
    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_ADMIN + "') || #publicId == principal.publicId")
    public EntityModel<UserResponseModel> getUser(@Parameter(description = "Id of the user") @PathVariable String publicId) {

        UserDto userDto = userService.getUserByPublicId(publicId);
        return HateoasBuilderUtil.getUserResponseModelHateoasFromDto(userDto);
    }


    @Operation(summary = "Update user", tags = "User management", description = "Use this endpoint to update a user",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was updated", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "User was not found", content = {@Content(mediaType = "application/json")})}
    )
    @PutMapping("/{publicId}")
    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_ADMIN + "') || #publicId == principal.publicId")
    public EntityModel<UserResponseModel> updateUser(@Parameter(description = "Id of the user to be updated") @PathVariable String publicId,
                                                     @Parameter(description = "New data for the user") @RequestBody UserUpdateRequestModel userReqModel) {

        UserDto userDto = userMapper.userUpdateRequestModelToDto(userReqModel);

        UserDto createdUserDto = userService.updateUser(publicId, userDto);

        return HateoasBuilderUtil.getUserResponseModelHateoasFromDto(createdUserDto);
    }
}
