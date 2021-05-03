package com.alexvait.accountingapi.accounting.model.dto;

import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import lombok.Data;

@Data
public class InvoiceDto {
    private long id;
    private String number;
    private String state;
    private long amount;
    private long customerId;
    private UserDto user;
    private String created;
}
