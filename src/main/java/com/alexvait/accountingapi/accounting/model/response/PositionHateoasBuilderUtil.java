package com.alexvait.accountingapi.accounting.model.response;

import com.alexvait.accountingapi.accounting.controller.InvoiceController;
import com.alexvait.accountingapi.accounting.controller.PositionController;
import com.alexvait.accountingapi.accounting.mapper.PositionMapper;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class PositionHateoasBuilderUtil {

    public static EntityModel<PositionResponseModel> getPositionHateoasFromDtoNoInvoiceLink(PositionDto positionDto) {
        return getPositionResponseModelHateoasFromDto(positionDto, true, false);
    }

    public static EntityModel<PositionResponseModel> getPositionHateoasFromDto(PositionDto positionDto) {
        return getPositionResponseModelHateoasFromDto(positionDto, false, true);
    }

    private static EntityModel<PositionResponseModel> getPositionResponseModelHateoasFromDto(PositionDto positionDto, boolean withSelfLink, boolean withInvoiceLink) {

        PositionResponseModel positionResponseModel = PositionMapper.INSTANCE.positionDtoToResponseModel(positionDto);

        EntityModel<PositionResponseModel> positionEm = EntityModel.of(positionResponseModel);

        if (withSelfLink) {
            Link selfLink = linkTo(methodOn(PositionController.class).getPosition(positionDto.getId())).withSelfRel();
            positionEm.add(selfLink);
        }

        if (positionDto.getInvoiceNumber() != null && withInvoiceLink) {
            positionEm.add(
                    linkTo(methodOn(InvoiceController.class).getInvoice(positionDto.getInvoiceNumber()))
                            .withRel("invoice"));
        }

        return positionEm;
    }
}
