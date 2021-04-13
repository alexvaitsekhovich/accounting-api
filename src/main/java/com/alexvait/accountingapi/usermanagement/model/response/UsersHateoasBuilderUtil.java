package com.alexvait.accountingapi.usermanagement.model.response;

import com.alexvait.accountingapi.accounting.controller.InvoiceController;
import com.alexvait.accountingapi.usermanagement.controller.UserController;
import com.alexvait.accountingapi.usermanagement.mapper.UserMapper;
import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class UsersHateoasBuilderUtil {

    public static EntityModel<UserResponseModel> getUserResponseModelHateoasFromDto(UserDto userDto) {

        UserResponseModel userResponseModel = UserMapper.INSTANCE.userDtoToResponseModel(userDto);

        Link selfLink = linkTo(methodOn(UserController.class).getUser(userDto.getPublicId())).withSelfRel();
        Link invoicesLink = linkTo(methodOn(InvoiceController.class).getInvoices()).withRel("invoices");

        return EntityModel.of(userResponseModel, Arrays.asList(selfLink, invoicesLink));
    }
}
