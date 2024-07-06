package com.chervonnaya.wallet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BadJsonException extends RuntimeException{
    private String message;
}
