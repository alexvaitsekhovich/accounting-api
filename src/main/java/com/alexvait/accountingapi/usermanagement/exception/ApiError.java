package com.alexvait.accountingapi.usermanagement.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ApiError {
    private final Map<String, Object> errorBody;

    public ApiError(HttpStatus httpStatus) {
        errorBody = new LinkedHashMap<>();
        errorBody.put("timestamp", LocalDateTime.now());
        errorBody.put("status", httpStatus.value());
    }

    public void setPath(String path) {
        errorBody.put("path", path);
    }

    public void setErrors(List<String> errors) {
        if (errors.size() == 0) {
            return;
        }

        errorBody.put("errors", errors);
    }

    public Map<String, Object> getErrorBody() {
        return errorBody;
    }
}
