package com.chervonnaya.wallet.controller;

import com.chervonnaya.wallet.dto.WalletOperationRequest;
import com.chervonnaya.wallet.model.Wallet;
import com.chervonnaya.wallet.service.WalletService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(WalletController.class)
public class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Test
    public void getBalance_Should_Succeed() throws Exception {
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet();
        wallet.setId(walletId);
        wallet.setBalance(BigDecimal.valueOf(1000));
        when(walletService.getWallet(walletId)).thenReturn(wallet);

        mockMvc.perform(get("/api/v1/wallet/" + walletId))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(walletId.toString()))
            .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    public void performOperation_Should_Succeed() throws Exception {
        UUID walletId = UUID.randomUUID();
        WalletOperationRequest request = new WalletOperationRequest();
        request.setId(walletId);
        request.setAmount(BigDecimal.valueOf(500));
        request.setOperation("DEPOSIT");
        doNothing().when(walletService).performOperation(any(WalletOperationRequest.class));

        mockMvc.perform(post("/api/v1/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @ParameterizedTest
    @MethodSource("argsProvider")
    public void performOperation_Should_FailWith400Error(String expectedMessage, WalletOperationRequest request) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/wallet")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andReturn();

        String actualMessage = result.getResponse().getContentAsString();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    static Stream<Arguments> argsProvider() {
        UUID walletId = UUID.randomUUID();

        return Stream.of(
            Arguments.of("id can't be null", createRequest("WITHDRAW", new BigDecimal(500), null)),
            Arguments.of("operation can't be blank", createRequest("", new BigDecimal(500), walletId)),
            Arguments.of("amount can't be null", createRequest("WITHDRAW", null, walletId))
        );
    }

    private static WalletOperationRequest createRequest(String operation, BigDecimal amount, UUID id) {
        WalletOperationRequest request = new WalletOperationRequest();
        request.setOperation(operation);
        request.setAmount(amount);
        request.setId(id);
        return request;
    }


}

