package com.alexvait.accountingapi.accounting.controller.annotated;

import com.alexvait.accountingapi.accounting.model.request.PositionCreateRequestModel;
import com.alexvait.accountingapi.accounting.model.response.PositionResponseModel;
import com.alexvait.accountingapi.security.config.AuthorityConstants;
import com.alexvait.accountingapi.security.config.SecurityConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Secured(SecurityConstants.ROLE_USER)
public interface PositionControllerAnnotated {
    @Operation(
            summary = "Get all open positions",
            tags = "Accounting positions",
            description = "Use this endpoint to get a list of all open positions for the authenticated user",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of positions was returned", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")})}
    )
    @PreAuthorize("hasAuthority('" + AuthorityConstants.MULTIPLE_POSITIONS_READ + "')")
    CollectionModel<EntityModel<PositionResponseModel>> getFreePositions();


    @Operation(
            summary = "Get position by id",
            tags = "Accounting positions",
            description = "Use this endpoint to get a position by id",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Position was returned", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "404", description = "Position not found", content = {@Content(mediaType = "application/json")})}
    )
    @PreAuthorize("hasAuthority('" + AuthorityConstants.POSITION_READ + "')")
    EntityModel<PositionResponseModel> getPosition(@Parameter(description = "Position id") @PathVariable long positionId);


    @Operation(
            summary = "Create a position",
            tags = "Accounting positions",
            description = "Use this endpoint to create am accounting position",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Position was created", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "400", description = "Invalid position data", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")})}
    )
    @PreAuthorize("hasAuthority('" + AuthorityConstants.POSITION_CREATE + "')")
    EntityModel<PositionResponseModel> createPosition(
            @Parameter(description = "Data of the position to be created") PositionCreateRequestModel positionReqModel
    );


    @Operation(
            summary = "Get valid payments",
            tags = "Accounting positions",
            description = "Use this endpoint to get the list of valid payments",
            security = @SecurityRequirement(name = SecurityConstants.AUTHORIZATION_HEADER)
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List was returned", content = {@Content(mediaType = "application/json")}),
            @ApiResponse(responseCode = "403", description = "Access denied", content = {@Content(mediaType = "application/json")})}
    )
    @PreAuthorize("hasAuthority('" + AuthorityConstants.LIST_PAYMENTS + "')")
    List<String> getPayments();

}
