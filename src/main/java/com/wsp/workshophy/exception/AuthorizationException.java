package com.wsp.workshophy.exception;

import com.wsp.workshophy.model.ApiMessageField;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "Required role was not provided")
public class AuthorizationException extends RuntimeException {

    private final String message;
    private final List<ApiMessageField> fields = new ArrayList<>();


    public AuthorizationException(String message) {
        super(message);
        this.message = message;
    }

    public AuthorizationException(String message, List<ApiMessageField> fields) {
        super(message);
        this.message = message;
        this.fields.addAll(fields);
    }
    @Override
    public String getMessage() {
        return message;
    }

    public List<ApiMessageField> getFields() {
        return fields;
    }
}
