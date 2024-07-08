package com.chervonnaya.wallet.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;

@RestControllerAdvice
public class RestControllerExceptionHandler {
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(WalletNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(final WalletNotFoundException e){
        ErrorResponse response = new ErrorResponse(
            String.format("Wallet %s not found", e.getWalletId()),
            HttpStatus.NOT_FOUND,
            now());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(InsufficientFundsException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientFundsException(final InsufficientFundsException e){
        ErrorResponse response = new ErrorResponse(
            String.format("Insufficient funds, wallet: %s", e.getWalletId()),
            HttpStatus.BAD_REQUEST,
            now());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BadJsonException.class)
    public ResponseEntity<ErrorResponse> handleBadJsonException(final BadJsonException e) {
        ErrorResponse response = new ErrorResponse(
            String.format("Invalid JSON input: %s", e.getMessage()),
            HttpStatus.BAD_REQUEST,
            now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getAllErrors()
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", "));
        ErrorResponse response = new ErrorResponse(
            String.format("Invalid JSON input: %s", errorMessage),
            HttpStatus.BAD_REQUEST,
            now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }



}
