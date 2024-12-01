package com.actvn.Shopee_BE.exception;

public class APIException extends RuntimeException {
    public APIException(String message) {
        super(message);
    }

    public APIException() {

    }
}
