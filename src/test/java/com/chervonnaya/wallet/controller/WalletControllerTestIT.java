package com.chervonnaya.wallet.controller;


import com.chervonnaya.wallet.dto.WalletOperationRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.jupiter.api.Test;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;


import java.io.IOException;
import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
@AllArgsConstructor
class WalletControllerTestIT {

    private static final String URL = "http://localhost:8080/api/v1/wallet";

    @Test
    void performOperation_Should_Succeed() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            StringEntity entity = new StringEntity("{\"id\":\"a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11\",\"operation\":\"WITHDRAW\",\"amount\":\"50\"}");
            entity.setContentType("application/json");
            HttpPost request = new HttpPost(URL);
            request.setHeader("Content-Type", "application/json");
            request.setEntity(entity);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                assertEquals(response.getStatusLine().getStatusCode(), 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void getBalance_Should_Succeed() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(URL + "/a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11");
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                assertEquals(response.getStatusLine().getStatusCode(), 200);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void loadTest()  {
        UUID id = UUID.fromString("ac037e1c-d53c-48f0-acfd-27cbf7c1e633");

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ObjectMapper objectMapper = new ObjectMapper();

        for (int i = 0; i < 1000; i++) {
            int operationIndex = i % 2;
            executorService.submit(() -> {
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    WalletOperationRequest request = new WalletOperationRequest();
                    request.setId(id);

                    if (operationIndex == 0) {
                        request.setOperation("DEPOSIT");
                        request.setAmount(new BigDecimal("100.0"));
                    } else {
                        request.setOperation("WITHDRAW");
                        request.setAmount(new BigDecimal("100.0"));
                    }

                    String json = objectMapper.writeValueAsString(request);

                    HttpPost httpPost = new HttpPost(URL);
                    HttpEntity entity = new StringEntity(json);
                    httpPost.setEntity(entity);
                    httpPost.setHeader("Content-Type", "application/json");

                    httpClient.execute(httpPost);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(URL + "/" + id);
            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);

            JsonNode rootNode = objectMapper.readTree(responseString);
            BigDecimal balance = new BigDecimal(rootNode.get("balance").asText());
            assertEquals(new BigDecimal("0.0"), balance);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}