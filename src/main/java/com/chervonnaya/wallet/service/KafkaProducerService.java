package com.chervonnaya.wallet.service;

import com.chervonnaya.wallet.dto.WalletOperationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final String TOPIC = "wallet_topic";
    private KafkaTemplate<String, WalletOperationRequest> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, WalletOperationRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(WalletOperationRequest request) {
        kafkaTemplate.send(TOPIC, request);
    }
}