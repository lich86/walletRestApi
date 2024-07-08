package com.chervonnaya.wallet.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WalletOperationRequest {
    @NotNull(message = "id can't be null")
    UUID id;
    @NotBlank(message = "operation can't be blank")
    String operation;
    @NotNull(message = "amount can't be null")
    @Digits(integer=19, fraction=2)
    BigDecimal amount;
}
