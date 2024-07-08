package com.chervonnaya.wallet.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
public class Wallet {

    @Id
    private UUID id;
    private BigDecimal balance;

}
