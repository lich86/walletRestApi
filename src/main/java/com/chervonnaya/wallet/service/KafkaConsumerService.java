package com.chervonnaya.wallet.service;

import com.chervonnaya.wallet.dto.WalletOperationRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class KafkaConsumerService {
    private final WalletService walletService;

    @KafkaListener(
        topicPartitions = @TopicPartition(topic = "wallet_topic", partitions = {"0", "1", "2"}),
        containerFactory = "kafkaListenerContainerFactory", groupId = "wallet"
    )
    public void consume(WalletOperationRequest request, Acknowledgment acknowledgment) {
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

        acknowledgment.acknowledge();

    }
}
