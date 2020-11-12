package com.alexvait.accountingapi.usermanagement.controller;

import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import com.alexvait.accountingapi.usermanagement.model.response.HateoasBuilder;
import com.alexvait.accountingapi.usermanagement.model.response.UserResponseModel;
import com.alexvait.accountingapi.usermanagement.service.UserService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users")
    public CollectionModel<EntityModel<UserResponseModel>> getUsers(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "5") int size) {

        List<UserDto> usersDto = userService.getUsers(page, size);

        Link selfLink = linkTo(methodOn(AdminController.class).getUsers(page, size)).withSelfRel();

        List<EntityModel<UserResponseModel>> users = usersDto.stream()
                .map(this::userDtoToResponseModelHateoas)
                .collect(Collectors.toList());

        return CollectionModel.of(users, selfLink);
    }

    private EntityModel<UserResponseModel> userDtoToResponseModelHateoas(UserDto userDto) {
        return HateoasBuilder.getUserResponseModelHateoasFromDto(userDto);
    }
}
