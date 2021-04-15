package com.alexvait.accountingapi.accounting.model.response.hateoas;

import com.alexvait.accountingapi.accounting.controller.InvoiceController;
import com.alexvait.accountingapi.accounting.mapper.InvoiceMapper;
import com.alexvait.accountingapi.accounting.model.dto.InvoiceDto;
import com.alexvait.accountingapi.accounting.model.response.InvoiceResponseModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import java.util.Arrays;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class InvoiceHateoasBuilderUtil {

    public static EntityModel<InvoiceResponseModel> getInvoiceResponseModelHateoasFromDto(InvoiceDto invoiceDto) {

        InvoiceResponseModel invoiceResponseModel = InvoiceMapper.INSTANCE.invoiceDtoToResponseModel(invoiceDto);

        Link selfLink = linkTo(methodOn(InvoiceController.class).getInvoice(invoiceDto.getNumber())).withSelfRel();
        Link positionsLink = linkTo(methodOn(InvoiceController.class).getBilledPositions(invoiceDto.getNumber())).withRel("positions");

        return EntityModel.of(invoiceResponseModel, selfLink, positionsLink);
    }
}
