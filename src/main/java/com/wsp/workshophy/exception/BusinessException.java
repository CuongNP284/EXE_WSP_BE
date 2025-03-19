package com.wsp.workshophy.exception;

import com.wsp.workshophy.model.ApiMessageField;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.List;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "")
public class BusinessException extends RuntimeException {

    private final String message;
    private final List<ApiMessageField> fields = new ArrayList<>();


    public BusinessException(String message) {
        super(message);
        this.message = message;
    }

    public BusinessException(String message, List<ApiMessageField> fields) {
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
