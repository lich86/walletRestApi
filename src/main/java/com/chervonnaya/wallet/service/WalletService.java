package com.chervonnaya.wallet.service;

import com.chervonnaya.wallet.exception.InsufficientFundsException;
import com.chervonnaya.wallet.model.Wallet;
import com.chervonnaya.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.beans.Transient;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
public class WalletService {

    WalletRepository walletRepository;

    @Autowired
    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public void deposit(UUID id, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(id).orElseThrow(NoSuchElementException::new);
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);
        log.debug(String.format("Deposit %s to wallet %s", amount, id));
    }

    @Transactional
    public void withdraw(UUID id, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(id).orElseThrow(NoSuchElementException::new);
        if(wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(id);
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);
        log.debug(String.format("Withdraw %s from wallet %s", amount, id));
    }

    public Wallet getWallet(UUID id) {
        return walletRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }
}
