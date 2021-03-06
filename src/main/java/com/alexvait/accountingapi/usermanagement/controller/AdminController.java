package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.usermanagement.controller.annotated.AdminControllerAnnotated;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.response.OperationResponse;
import com.alexvait.accountingapi.usermanagement.model.response.ResponseOperationState;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(AdminController.BASE_URL)
@CrossOrigin(origins = "*")
public class AdminController implements AdminControllerAnnotated {

    public static final String BASE_URL = "/api/v1/admin";

    public static final String defaultAdminPageNumber = "0";
    public static final String defaultAdminPageSize = "5";

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Override
    @GetMapping("/user")
    public CollectionModel<EntityModel<UserResponseModel>> getUsers(
            @RequestParam(value = "page", required = false, defaultValue = defaultAdminPageNumber) int page,
            @RequestParam(value = "size", required = false, defaultValue = defaultAdminPageSize) int size) {

        List<UserDto> usersDto = userService.getUsers(page, size);

        Link selfLink = linkTo(methodOn(AdminController.class).getUsers(page, size)).withSelfRel();

        List<EntityModel<UserResponseModel>> users = usersDto.stream()
                .map(this::userDtoToResponseModelHateoas)
                .collect(Collectors.toList());

        return CollectionModel.of(users, selfLink);
    }

    @Override
    @DeleteMapping("/user/{publicId}")
    public OperationResponse deleteUser(@PathVariable String publicId) {

        userService.deleteUserByPublicId(publicId);
        return new OperationResponse(ResponseOperationState.SUCCESS, HttpStatus.OK);
    }
}
