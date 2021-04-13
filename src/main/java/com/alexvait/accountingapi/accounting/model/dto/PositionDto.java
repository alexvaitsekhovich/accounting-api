package com.alexvait.accountingapi.accounting.model.dto;

import com.alexvait.accountingapi.accounting.entity.InvoiceEntity;
import com.alexvait.accountingapi.accounting.entity.enums.Payment;
import com.alexvait.accountingapi.usermanagement.entity.UserEntity;
import lombok.Data;

@Data
public class PositionDto {
    private long id;
    private long amount;
    private Payment payment;
    private long customerId;
    private UserEntity userId;
    private InvoiceEntity invoice;
}
