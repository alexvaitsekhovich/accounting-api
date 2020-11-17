package com.alexvait.accountingapi.usermanagement.controller.annotated;

import com.alexvait.accountingapi.security.config.SecurityConstants;
import com.alexvait.accountingapi.usermanagement.model.request.UserCreateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.request.UserUpdateRequestModel;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;

public interface UserControllerAnnotated {
    @Operation(summary = "Create a user", tags = "User management", description = "Use this endpoint to create a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User was created", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "409", description = "Email is already registered", content = {@Content(mediaType = "application/json")})}
    )
    EntityModel<UserResponseModel> createUser(
            @Parameter(description = "Data of the user to be created") UserCreateRequestModel userReqModel
    );



    @Operation(summary = "Get user", tags = "User management", description = "Use this endpoint to get a user details",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was found", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "User was not found", content = {@Content(mediaType = "application/json")})}
    )
    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_ADMIN + "') || #publicId == principal.publicId")
    EntityModel<UserResponseModel> getUser(
            @Parameter(description = "Id of the user") String publicId
    );



    @Operation(summary = "Update user", tags = "User management", description = "Use this endpoint to update a user",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was updated", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "User was not found", content = {@Content(mediaType = "application/json")})}
    )
    @PreAuthorize("hasRole('" + SecurityConstants.ROLE_ADMIN + "') || #publicId == principal.publicId")
    EntityModel<UserResponseModel> updateUser(
            @Parameter(description = "Id of the user to be updated") String publicId,
            @Parameter(description = "New data for the user") UserUpdateRequestModel userReqModel
    );
}
