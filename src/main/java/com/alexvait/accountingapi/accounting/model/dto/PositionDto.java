package com.alexvait.accountingapi.accounting.model.dto;

import com.alexvait.accountingapi.accounting.entity.enums.Payment;
import lombok.Data;

@Data
public class PositionDto {
    private long id;
    private long amount;
    private Payment payment;
    private long customerId;
    private String invoiceNumber;
}
