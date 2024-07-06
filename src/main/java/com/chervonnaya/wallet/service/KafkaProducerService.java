package com.chervonnaya.wallet.service;

import com.chervonnaya.wallet.dto.WalletOperationRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@AllArgsConstructor
public class KafkaProducerService {

    private static final String TOPIC = "wallet_topic";
    private final KafkaTemplate<String, WalletOperationRequest> kafkaTemplate;


    public void sendMessage(WalletOperationRequest request, UUID walletId) {
        kafkaTemplate.send(TOPIC, String.valueOf(walletId), request);
    }
}