package com.chervonnaya.wallet.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletOperationRequest {
    UUID id;
    String operation;
    BigDecimal amount;
}
