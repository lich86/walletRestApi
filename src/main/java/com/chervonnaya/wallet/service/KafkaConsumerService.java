package com.chervonnaya.wallet.service;

import com.chervonnaya.wallet.dto.WalletOperationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaConsumerService {
    private final WalletService walletService;

    @Autowired
    public KafkaConsumerService(WalletService walletService) {
        this.walletService = walletService;
    }

    @KafkaListener(topics = "wallet_topic", groupId = "wallet")
    public void consume(WalletOperationRequest request) {
        switch (request.getOperation()) {
            case "DEPOSIT":
                walletService.deposit(request.getId(), request.getAmount());

                break;
            case "WITHDRAW":
                walletService.withdraw(request.getId(), request.getAmount());
                log.debug(String.format("Withdraw %s from wallet %s", request.getAmount(), request.getId()));
                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + request.getOperation());
        }

    }
}
