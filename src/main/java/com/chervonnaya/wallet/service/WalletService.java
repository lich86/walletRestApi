package com.chervonnaya.wallet.service;

import com.chervonnaya.wallet.dto.WalletOperationRequest;
import com.chervonnaya.wallet.exception.BadJsonException;
import com.chervonnaya.wallet.exception.InsufficientFundsException;
import com.chervonnaya.wallet.exception.WalletNotFoundException;
import com.chervonnaya.wallet.model.Wallet;
import com.chervonnaya.wallet.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class WalletService {

    WalletRepository walletRepository;

    @Transactional
    public void performOperation(WalletOperationRequest request) throws WalletNotFoundException, InsufficientFundsException, BadJsonException {
        switch (request.getOperation()) {
            case "DEPOSIT":
                deposit(request.getId(), request.getAmount());
                log.debug(String.format("Deposit %s from wallet %s", request.getAmount(), request.getId()));
                break;
            case "WITHDRAW":
                withdraw(request.getId(), request.getAmount());
                log.debug(String.format("Withdraw %s from wallet %s", request.getAmount(), request.getId()));
                break;
            default:
                throw new BadJsonException("Unknown operation: " + request.getOperation());
        }

    }

    @Transactional
    protected void deposit(UUID id, BigDecimal amount) throws WalletNotFoundException{
        Wallet wallet;
        try {
            wallet = getWallet(id);
            wallet.setBalance(wallet.getBalance().add(amount));
            walletRepository.save(wallet);
            log.debug(String.format("Deposit %s to wallet %s", amount, id));
        } catch (NoSuchElementException e) {
            throw new WalletNotFoundException(id.toString());
        }
    }

    @Transactional
    protected void withdraw(UUID id, BigDecimal amount) throws WalletNotFoundException, InsufficientFundsException {
        Wallet wallet;
        try {
            wallet = getWallet(id);
            if (wallet.getBalance().compareTo(amount) < 0) {
                throw new InsufficientFundsException(id.toString());
            }
            wallet.setBalance(wallet.getBalance().subtract(amount));
            walletRepository.save(wallet);
            log.debug(String.format("Withdraw %s from wallet %s", amount, id));
        } catch (NoSuchElementException e) {
            throw new WalletNotFoundException(id.toString());
        }
    }

    public Wallet getWallet(UUID id) throws WalletNotFoundException {
        try {
            return walletRepository.findById(id).orElseThrow(NoSuchElementException::new);
        } catch (NoSuchElementException e) {
            throw new WalletNotFoundException(id.toString());
        }

    }

}
