package com.alexvait.accountingapi.usermanagement.controller.annotated;

import com.alexvait.accountingapi.security.config.AuthorityConstants;
import com.alexvait.accountingapi.security.config.SecurityConstants;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.response.UsersHateoasBuilderUtil;
import com.alexvait.accountingapi.usermanagement.model.response.OperationResponse;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;

public interface AdminControllerAnnotated {
    @Operation(
            summary = "Get all users",
            tags = "Administration",
            description = "Use this endpoint to get a list of users with pagination",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users was created", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")})}
    )
    @PreAuthorize("hasAuthority('" + AuthorityConstants.MULTIPLE_USERS_READ + "')")
    CollectionModel<EntityModel<UserResponseModel>> getUsers(
            @Parameter(description = "Page number") int page,
            @Parameter(description = "Page size") int size);



    @Operation(
            summary = "Delete user",
            tags = "Administration",
            description = "Use this endpoint to delete a user",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User was deleted", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")})}
    )
    @PreAuthorize("hasAuthority('" + AuthorityConstants.USER_DELETE + "')")
    OperationResponse deleteUser(@Parameter(description = "User id") @PathVariable String publicId);



    default EntityModel<UserResponseModel> userDtoToResponseModelHateoas(UserDto userDto) {
        return UsersHateoasBuilderUtil.getUserResponseModelHateoasFromDto(userDto);
    }
}
