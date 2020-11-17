package com.alexvait.accountingapi.usermanagement.exception;

import com.alexvait.accountingapi.usermanagement.model.response.OperationResponse;
import com.alexvait.accountingapi.usermanagement.model.response.ResponseOperationState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Test OperationResponse")
class OperationResponseTest {

    @Test
    @DisplayName("Test setting messages")
    void testSettingMessages() {
        // assign
        OperationResponse operationResponse = new OperationResponse(ResponseOperationState.SUCCESS, HttpStatus.OK);
        operationResponse.setMessages(Arrays.asList("Message1", "Message2"));

        // act
        Map<String, Object> body = operationResponse.getDetails();

        // assert
        Object operationBody = body.get("messages");

        assertAll(
                "test OperationResponse messages and type",
                () -> assertThat("instanceOf failed", operationBody, instanceOf(List.class)),
                () -> assertThat("hasSize failed", (List<?>) operationBody, hasSize(2))
        );
    }

    @Test
    @DisplayName("Test getting status")
    void testStatus() {
        // assign
        OperationResponse operationResponse1 = new OperationResponse(ResponseOperationState.SUCCESS, HttpStatus.OK);
        OperationResponse operationResponse2 = new OperationResponse(ResponseOperationState.FAILURE, HttpStatus.BAD_REQUEST);

        // assert
        assertAll(
                "test OperationResponse states",
                () -> assertEquals(200, operationResponse1.getDetails().get("status"), "status 200 failed"),
                () -> assertEquals(400, operationResponse2.getDetails().get("status"), "status 400 failed")
        );
    }

}

