package com.alexvait.accountingapi.usermanagement.model.response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@DisplayName("Test OperationResponse")
class OperationResponseTest {

    private OperationResponse operationResponse;

    @BeforeEach
    void setUp() {
        operationResponse = new OperationResponse(ResponseOperationState.SUCCESS, HttpStatus.OK);
    }

    @Test
    @DisplayName("Test setting messages")
    void testSetMessages() {
        // assign
        operationResponse.setMessages(Arrays.asList("Message1", "Message1"));

        // act
        Map<String, Object> details = operationResponse.getDetails();

        // assert
        assertThat(details.get("messages"), instanceOf(List.class));
        assertThat((List<?>) details.get("messages"), hasSize(2));
    }

    @Test
    @DisplayName("Test setting no messages")
    void testSetEmptyMessages() {
        // assign
        operationResponse.setMessages(Collections.emptyList());

        // act

        // assert
        assertThat(operationResponse.getDetails().get("messages"), nullValue());
    }

    @Test
    @DisplayName("Test setting path")
    void testSetPath() {
        // assign
        String path = "somepath";
        operationResponse.setPath(path);

        // act
        String savedPath = (String) operationResponse.getDetails().get("path");

        // assert
        assertThat(savedPath, equalTo(path));
    }
}