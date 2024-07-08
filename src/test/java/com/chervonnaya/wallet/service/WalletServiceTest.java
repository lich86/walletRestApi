package com.chervonnaya.wallet.service;

import com.chervonnaya.wallet.dto.WalletOperationRequest;
import com.chervonnaya.wallet.exception.BadJsonException;
import com.chervonnaya.wallet.exception.InsufficientFundsException;
import com.chervonnaya.wallet.exception.WalletNotFoundException;
import com.chervonnaya.wallet.model.Wallet;
import com.chervonnaya.wallet.repository.WalletRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @InjectMocks
    private WalletService walletService;

    AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void closeService() throws Exception {
        closeable.close();
    }

    @Test
    void deposit_Should_Succeed() throws WalletNotFoundException {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100.0);
        Wallet existingWallet = createWallet(walletId, BigDecimal.ZERO);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(existingWallet));

        walletService.deposit(walletId, amount);

        assertEquals(BigDecimal.valueOf(100.0), existingWallet.getBalance());
        verify(walletRepository, times(1)).save(existingWallet);
    }

    @Test
    void withdraw_Should_Succeed() throws WalletNotFoundException, InsufficientFundsException {
        UUID walletId = UUID.randomUUID();
        BigDecimal initialBalance = BigDecimal.valueOf(200.0);
        BigDecimal withdrawAmount = BigDecimal.valueOf(100.0);
        Wallet existingWallet = createWallet(walletId, initialBalance);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(existingWallet));

        walletService.withdraw(walletId, withdrawAmount);

        assertEquals(BigDecimal.valueOf(100.0), existingWallet.getBalance());
        verify(walletRepository, times(1)).save(existingWallet);
    }

    @Test
    void withdrawInsufficientFunds_Should_ThrowInsufficientFundsException() {
        UUID walletId = UUID.randomUUID();
        BigDecimal initialBalance = BigDecimal.valueOf(50.0);
        BigDecimal withdrawAmount = BigDecimal.valueOf(100.0);
        Wallet existingWallet = createWallet(walletId, initialBalance);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(existingWallet));

        assertThrows(InsufficientFundsException.class, () -> walletService.withdraw(walletId, withdrawAmount));
        assertEquals(initialBalance, existingWallet.getBalance());
        verify(walletRepository, never()).save(existingWallet);
    }

    @Test
    void performOperationDeposit_Should_Succeed() throws WalletNotFoundException, InsufficientFundsException, BadJsonException {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100.0);
        WalletOperationRequest request = createOperationRequest(walletId, "DEPOSIT", amount);
        Wallet existingWallet = createWallet(walletId, BigDecimal.ZERO);
        when(walletRepository.findById(walletId)).thenReturn(Optional.of(existingWallet));

        walletService.performOperation(request);

        assertEquals(BigDecimal.valueOf(100.0), existingWallet.getBalance());
        verify(walletRepository, times(1)).save(existingWallet);
    }

    @Test
    void performOperation_Should_ThrowUnknownOperationException() {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100.0);
        WalletOperationRequest request = createOperationRequest(walletId, "D", amount);

        assertThrows(BadJsonException.class, () -> walletService.performOperation(request));
        verify(walletRepository, never()).save(any());
    }

    @Test
    void performOperationWallet_Should_ThrowNotFoundException() {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(100.0);
        WalletOperationRequest request = createOperationRequest(walletId, "DEPOSIT", amount);
        when(walletRepository.findById(walletId)).thenReturn(Optional.empty());

        assertThrows(WalletNotFoundException.class, () -> walletService.performOperation(request));
        verify(walletRepository, never()).save(any());
    }

    private Wallet createWallet(UUID id, BigDecimal balance) {
        Wallet wallet = new Wallet();
        wallet.setId(id);
        wallet.setBalance(balance);
        return wallet;
    }

    private WalletOperationRequest createOperationRequest(UUID walletId, String operation, BigDecimal amount) {
        WalletOperationRequest request = new WalletOperationRequest();
        request.setId(walletId);
        request.setOperation(operation);
        request.setAmount(amount);
        return request;
    }
}
