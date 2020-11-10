package com.alexvait.accountingapi.security.model.response;

import com.alexvait.accountingapi.security.model.request.RequestOperations;
import lombok.Data;

@Data
public class OperationResponseModel {
    private RequestOperations operation;
    private ResponseOperationStates result;

    public OperationResponseModel(RequestOperations operation) {
        this.operation = operation;
    }
}
