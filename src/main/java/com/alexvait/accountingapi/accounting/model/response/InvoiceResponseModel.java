package com.alexvait.accountingapi.accounting.model.response;

import com.alexvait.accountingapi.accounting.entity.enums.InvoiceState;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "invoices", itemRelation = "invoice")
public class InvoiceResponseModel {
    private String number;
    private InvoiceState state;
    private long amount;
    private long customerId;
}
