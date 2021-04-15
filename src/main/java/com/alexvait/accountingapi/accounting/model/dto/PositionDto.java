package com.alexvait.accountingapi.accounting.model.dto;

import lombok.Data;

@Data
public class PositionDto {
    private long id;
    private long amount;
    private String payment;
    private String label;
    private long customerId;
    private String invoiceNumber;

    public void setPayment(String payment) {
        this.payment = payment.toUpperCase();
    }
}
