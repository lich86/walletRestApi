package com.chervonnaya.wallet.controller;

import com.chervonnaya.wallet.dto.WalletOperationRequest;
import com.chervonnaya.wallet.exception.WalletNotFoundException;
import com.chervonnaya.wallet.model.Wallet;
import com.chervonnaya.wallet.service.KafkaProducerService;
import com.chervonnaya.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(value = "/api/v1/wallet")
public class WalletController {

    @Autowired
    private KafkaProducerService producerService;
    @Autowired
    private WalletService walletService;

    @GetMapping(value = "/{id}")
    @ResponseBody
    public Wallet getBalance(@PathVariable(name = "id") UUID id) throws WalletNotFoundException {
        return walletService.getWallet(id);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Void> performOperation(@RequestBody WalletOperationRequest request) {
        CompletableFuture<Boolean> future = producerService.sendMessage(request, request.getId());
        try {
            boolean isSuccess = future.get();

            if (isSuccess) {
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


}
