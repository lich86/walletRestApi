package com.chervonnaya.wallet.service;

import com.chervonnaya.wallet.dto.WalletOperationRequest;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Service
@AllArgsConstructor
public class KafkaProducerService {

    private static final String TOPIC = "wallet_topic";
    private final KafkaTemplate<String, WalletOperationRequest> kafkaTemplate;

    @Async
    public CompletableFuture<Boolean> sendMessage(WalletOperationRequest request, UUID walletId) {
        try {
            kafkaTemplate.send(TOPIC, String.valueOf(walletId), request);
            return CompletableFuture.completedFuture(true);
        } catch (Exception e) {
            return CompletableFuture.completedFuture(false);
        }
    }
}