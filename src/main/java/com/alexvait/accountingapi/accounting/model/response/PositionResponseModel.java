package com.alexvait.accountingapi.accounting.model.response;

import com.alexvait.accountingapi.accounting.entity.InvoiceEntity;
import com.alexvait.accountingapi.accounting.entity.enums.Payment;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.hateoas.server.core.Relation;

@Data
@Relation(collectionRelation = "positions", itemRelation = "position")
public class PositionResponseModel {
    private long amount;
    private Payment payment;
    private long customerId;
    @JsonIgnore
    private InvoiceEntity invoice;
}
