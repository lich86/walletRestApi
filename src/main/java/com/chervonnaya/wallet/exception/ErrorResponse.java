package com.chervonnaya.wallet.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {
    private final String message;
    private final int status;
    private final String error;
    private final LocalDateTime timestamp;

    public ErrorResponse(String message, HttpStatus status, LocalDateTime timestamp) {
        this.message = message;
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.timestamp = timestamp;
    }
}
