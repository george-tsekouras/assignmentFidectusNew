package com.fidectus.eventlog.dto;

import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class ApiErrorDto {

    private HttpStatus status;
    private String message;
    private List<String> errors;

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public ApiErrorDto(final HttpStatus status, final String message){
        super();
        this.status = status;
        this.message = message;
        this.errors = new ArrayList<>();
    }
}