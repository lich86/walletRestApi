package com.chervonnaya.wallet.controller;

import com.chervonnaya.wallet.dto.WalletOperationRequest;
import com.chervonnaya.wallet.model.Wallet;
import com.chervonnaya.wallet.service.KafkaProducerService;
import com.chervonnaya.wallet.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api/v1/wallet")
public class WalletController {

    @Autowired
    private KafkaProducerService producerService;
    @Autowired
    private WalletService walletService;

    @GetMapping(value = "/{id}")
    @ResponseBody
    public Wallet getBalance(@PathVariable(name = "id") UUID id) {
        return walletService.getWallet(id);
    }

    @PostMapping
    @ResponseBody
    public void performOperation(@RequestBody WalletOperationRequest request) {
        producerService.sendMessage(request, request.getId());
    }


}
