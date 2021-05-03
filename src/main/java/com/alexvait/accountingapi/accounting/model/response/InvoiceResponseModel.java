package com.alexvait.accountingapi.accounting.model.response;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "invoices", itemRelation = "invoice")
public class InvoiceResponseModel {
    private String number;
    private String state;
    private long amount;
    private long customerId;
    private String created;
}
