package com.alexvait.accountingapi.usermanagement.exception;

import com.alexvait.accountingapi.usermanagement.model.response.ResponseOperationState;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Getter
public class OperationResponse {
    private final Map<String, Object> details;

    private ResponseOperationState responseState;
    private HttpStatus httpStatus;

    public OperationResponse(ResponseOperationState responseState, HttpStatus httpStatus) {
        this.responseState = Objects.requireNonNull(responseState);
        this.httpStatus = Objects.requireNonNull(httpStatus);

        details = new LinkedHashMap<>();
        details.put("timestamp", LocalDateTime.now());
        details.put("status", httpStatus.value());
    }

    public void setPath(String path) {
        details.put("path", path);
    }

    public void setMessages(List<String> messages) {
        if (messages.size() == 0) {
            return;
        }

        details.put("messages", messages);
    }
}
