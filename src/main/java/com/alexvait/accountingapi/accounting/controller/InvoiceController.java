package com.alexvait.accountingapi.accounting.controller;

import com.alexvait.accountingapi.usermanagement.model.dto.UserDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounting")
public class InvoiceController {

    @GetMapping("/invoices/{userPublicId}")
    public UserDto getInvoices(@PathVariable String userPublicId) {
        return new UserDto();
    }
}
