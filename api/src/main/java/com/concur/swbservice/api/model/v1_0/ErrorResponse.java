package com.concur.swbservice.api.model.v1_0;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;

/**
 * Created by jmunday on 7/22/15.
 */
public class ErrorResponse implements Serializable {

    long timestamp;
    int status;
    String[] errors;
    String exception;
    String message;
    String path;

    public ErrorResponse(HttpStatus httpStatus, HttpServletRequest httpServletRequest, Throwable exception, String... errors) {
        this.timestamp = System.currentTimeMillis();
        this.status = httpStatus.value();
        this.exception = exception.getClass().getName();
        this.message = exception.getMessage();
        this.errors = errors;
        this.path = httpServletRequest.getServletPath();
    }

    public ErrorResponse(HttpStatus httpStatus, HttpServletRequest httpServletRequest, String... errors) {
        this.timestamp = System.currentTimeMillis();
        this.status = httpStatus.value();
        this.errors = errors;
        this.path = httpServletRequest.getServletPath();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String[] getErrors() {
        return errors;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getException() {
        return exception;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
