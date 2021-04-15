package com.alexvait.accountingapi.accounting.model.request;

import com.alexvait.accountingapi.accounting.entity.PositionEntity;
import com.alexvait.accountingapi.accounting.model.validation.ValueOfEnum;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PositionCreateRequestModel {
    private long amount = 0;

    @NotNull(message = "Payment is mandatory")
    @ValueOfEnum(enumClass = PositionEntity.Payment.class)
    private String payment;

    @NotNull(message = "Label is mandatory")
    private String label;

    private long customerId = 0;
}
