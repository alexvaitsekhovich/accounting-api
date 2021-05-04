package com.alexvait.accountingapi.accounting.controller;

import com.alexvait.accountingapi.accounting.controller.annotated.InvoiceControllerAnnotated;
import com.alexvait.accountingapi.accounting.model.dto.InvoiceDto;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;
import com.alexvait.accountingapi.accounting.model.response.InvoiceResponseModel;
import com.alexvait.accountingapi.accounting.model.response.PositionResponseModel;
import com.alexvait.accountingapi.accounting.model.response.hateoas.InvoiceHateoasBuilderUtil;
import com.alexvait.accountingapi.accounting.model.response.hateoas.InvoiceResponseModelPagedList;
import com.alexvait.accountingapi.accounting.model.response.hateoas.PositionHateoasBuilderUtil;
import com.alexvait.accountingapi.accounting.service.InvoiceService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(InvoiceController.BASE_URL)
@CrossOrigin(origins = "*")
public class InvoiceController implements InvoiceControllerAnnotated {

    public static final String BASE_URL = "/api/v1/invoice";

    public static final String defaultInvoicesPageNumber = "0";

    public static final String defaultInvoicesPageSize = "5";

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public InvoiceResponseModelPagedList getInvoices(
            @RequestParam(value = "page", required = false, defaultValue = defaultInvoicesPageNumber) int page,
            @RequestParam(value = "size", required = false, defaultValue = defaultInvoicesPageSize) int size) {

        return invoiceService.getInvoices(page, size);
    }

    @GetMapping("/{invoiceNr}")
    public EntityModel<InvoiceResponseModel> getInvoice(@PathVariable String invoiceNr) {
        InvoiceDto invoiceDto = invoiceService.getInvoice(invoiceNr);
        return InvoiceHateoasBuilderUtil.getInvoiceResponseModelHateoasFromDto(invoiceDto);
    }

    @GetMapping("/{invoiceNr}/positions")
    public CollectionModel<EntityModel<PositionResponseModel>> getBilledPositions(@PathVariable String invoiceNr) {

        List<PositionDto> positionsDto = invoiceService.getPositions(invoiceNr);

        List<EntityModel<PositionResponseModel>> positions = positionsDto.stream()
                .map(PositionHateoasBuilderUtil::getPositionHateoasFromDtoNoInvoiceLink)
                .collect(Collectors.toList());

        return CollectionModel.of(positions);
    }

    @PostMapping("/generate")
    public EntityModel<InvoiceResponseModel> createInvoice() {
        InvoiceDto invoiceDto = invoiceService.generateInvoice();
        return InvoiceHateoasBuilderUtil.getInvoiceResponseModelHateoasFromDto(invoiceDto);
    }

    public InvoiceResponseModelPagedList getInvoices() {
        return getInvoices(Integer.parseInt(defaultInvoicesPageNumber), Integer.parseInt(defaultInvoicesPageSize));
    }
}
