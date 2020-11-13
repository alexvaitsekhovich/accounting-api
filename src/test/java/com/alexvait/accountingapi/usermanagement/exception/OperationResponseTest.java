package com.alexvait.accountingapi.usermanagement.exception;

import com.alexvait.accountingapi.usermanagement.model.response.ResponseOperationState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
        assertThat(operationBody, instanceOf(List.class));
        assertThat((List<?>) operationBody, hasSize(2));
    }

    @Test
    @DisplayName("Test getting status")
    void testStatus() {
        // assign
        OperationResponse operationResponse1 = new OperationResponse(ResponseOperationState.SUCCESS, HttpStatus.OK);
        OperationResponse operationResponse2 = new OperationResponse(ResponseOperationState.FAILURE, HttpStatus.BAD_REQUEST);

        // assert
        assertThat(operationResponse1.getDetails().get("status"), equalTo(200));
        assertThat(operationResponse2.getDetails().get("status"), equalTo(400));
    }

}

