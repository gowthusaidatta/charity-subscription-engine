package com.digitalheroes.golfcharity.common;

public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(message);
    }
}
