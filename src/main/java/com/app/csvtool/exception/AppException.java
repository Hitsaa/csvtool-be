package com.app.csvtool.exception;

import org.springframework.http.HttpStatus;

public class AppException extends RuntimeException {

    private static final long serialVersionUID = 7162330549370241516L;
    private HttpStatus status;
    private int code = -1;

    public AppException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public AppException(String message, int code, HttpStatus status) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getCode() { return code;}
}

