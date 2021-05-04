package com.alexvait.accountingapi.accounting.service;

import com.alexvait.accountingapi.accounting.model.dto.InvoiceDto;
import com.alexvait.accountingapi.accounting.model.dto.PositionDto;
import com.alexvait.accountingapi.accounting.model.response.hateoas.InvoiceResponseModelPagedList;

import java.util.List;

public interface InvoiceService {
    InvoiceResponseModelPagedList getInvoices(int page, int size);

    InvoiceDto getInvoice(String number);

    List<PositionDto> getPositions(String number);

    InvoiceDto generateInvoice();
}
