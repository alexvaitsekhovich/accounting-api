package com.alexvait.accountingapi.accounting.exception;

public class InvoiceNotFoundException extends NotFoundException {
    public InvoiceNotFoundException(String message) {
        super(message);
    }
}
