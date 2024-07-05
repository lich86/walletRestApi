package com.chervonnaya.wallet.exception;

import java.util.UUID;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(UUID id) {
        super("Insufficient Funds: " + id);
    }
}
