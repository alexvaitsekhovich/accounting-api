package com.alexvait.accountingapi.accounting.model.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test PositionDto")
class PositionDtoTest {

    @Test
    @DisplayName("Test equals()")
    void testEquals() {
        // arrange
        PositionDto positionDto1 = getTestPositionDto();
        PositionDto positionDto2 = getTestPositionDto();

        PositionDto positionDto3 = getTestPositionDto();
        positionDto3.setLabel("X");

        // act, assert
        assertAll(
                "test PositionDto equals",
                () -> assertEquals(positionDto1, positionDto1, "equals failed"),
                () -> assertEquals(positionDto1, positionDto2, "equals 1-2 failed"),
                () -> assertNotEquals(positionDto1, positionDto3, "equals 1-3 failed"),
                () -> assertNotEquals(positionDto2, positionDto3, "not equals 2-3 failed")
        );
    }

    @Test
    @DisplayName("Test hashCode()")
    void testHashCode() {

        // arrange
        PositionDto positionDto1 = getTestPositionDto();
        PositionDto positionDto2 = getTestPositionDto();

        // act, assert
        assertAll(
                "test PositionDto hashCode",
                () -> assertEquals(positionDto1.hashCode(), positionDto1.hashCode(), "hashCode self failed"),
                () -> assertEquals(positionDto1.hashCode(), positionDto2.hashCode(), "hashCode failed")
        );

    }

    private PositionDto getTestPositionDto() {
        PositionDto positionDto = new PositionDto();
        positionDto.setId(1);
        positionDto.setAmount(111);
        positionDto.setCustomerId(11);
        positionDto.setLabel("Test");
        positionDto.setInvoiceNumber("1");
        positionDto.setPayment("cash");
        return positionDto;
    }
}