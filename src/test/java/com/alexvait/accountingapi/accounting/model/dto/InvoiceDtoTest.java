package com.alexvait.accountingapi.accounting.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test InvoiceDto")
class InvoiceDtoTest {

    @Test
    @DisplayName("Test equals()")
    void testEquals() {
        // arrange
        InvoiceDto invoiceDto1 = getTestInvoiceDto();
        InvoiceDto invoiceDto2 = getTestInvoiceDto();

        InvoiceDto invoiceDto3 = getTestInvoiceDto();
        invoiceDto3.setAmount(222);

        // act, assert
        assertAll(
                "test PositionDto equals",
                () -> assertEquals(invoiceDto1, invoiceDto1, "equals failed"),
                () -> assertEquals(invoiceDto1, invoiceDto2, "equals 1-2 failed"),
                () -> assertNotEquals(invoiceDto1, invoiceDto3, "equals 1-3 failed"),
                () -> assertNotEquals(invoiceDto2, invoiceDto3, "not equals 2-3 failed")
        );
    }

    @Test
    @DisplayName("Test hashCode()")
    void testHashCode() {

        // arrange
        InvoiceDto invoiceDto1 = getTestInvoiceDto();
        InvoiceDto invoiceDto2 = getTestInvoiceDto();

        // act, assert
        assertAll(
                "test PositionDto hashCode",
                () -> assertEquals(invoiceDto1.hashCode(), invoiceDto1.hashCode(), "hashCode self failed"),
                () -> assertEquals(invoiceDto1.hashCode(), invoiceDto2.hashCode(), "hashCode failed")
        );

    }

    private InvoiceDto getTestInvoiceDto() {
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setId(1);
        invoiceDto.setAmount(111);
        invoiceDto.setCustomerId(11);
        invoiceDto.setState("open");
        return invoiceDto;
    }
}