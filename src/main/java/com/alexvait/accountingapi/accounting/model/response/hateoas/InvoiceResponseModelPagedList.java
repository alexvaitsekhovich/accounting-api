package com.alexvait.accountingapi.accounting.model.response.hateoas;

import com.alexvait.accountingapi.accounting.model.response.InvoiceResponseModel;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;

import java.util.List;

public class InvoiceResponseModelPagedList extends PageImpl<EntityModel<InvoiceResponseModel>> {

    public InvoiceResponseModelPagedList(List<EntityModel<InvoiceResponseModel>> content, Pageable pageable, long total) {
        super(content, pageable, total);
    }
}
