package com.chervonnaya.wallet.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class WalletNotFoundException extends RuntimeException{
    private String walletId;
}
