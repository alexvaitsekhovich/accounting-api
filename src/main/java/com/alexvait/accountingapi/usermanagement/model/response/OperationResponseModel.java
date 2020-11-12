package com.alexvait.accountingapi.usermanagement.model.response;

import com.alexvait.accountingapi.usermanagement.model.request.RequestOperations;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OperationResponseModel {
    private RequestOperations operation;
    private ResponseOperationStates result;

    public OperationResponseModel(RequestOperations operation) {
        this.operation = operation;
    }
}
