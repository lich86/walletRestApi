package com.chervonnaya.wallet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class InsufficientFundsException extends Exception{
    private String walletId;
}
