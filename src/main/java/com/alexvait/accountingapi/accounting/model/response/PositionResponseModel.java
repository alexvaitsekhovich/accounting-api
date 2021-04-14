package com.alexvait.accountingapi.accounting.model.response;

import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

import java.util.Locale;

@Data
@Relation(collectionRelation = "positions", itemRelation = "position")
public class PositionResponseModel {
    private long id;
    private long amount;
    private String payment;
    private long customerId;
    private String invoiceNumber;

    public void setPayment(String payment) {
        this.payment = payment.toLowerCase();
    }
}
