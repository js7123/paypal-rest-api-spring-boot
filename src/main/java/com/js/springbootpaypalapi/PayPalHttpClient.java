package com.js.springbootpaypalapi;

import com.js.springbootpaypalapi.config.PayPalConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.js.springbootpaypalapi.dto.AccessTokenResponseDTO;
import com.js.springbootpaypalapi.dto.ClientTokenDTO;
import com.js.springbootpaypalapi.dto.OrderDTO;
import com.js.springbootpaypalapi.dto.OrderResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.js.springbootpaypalapi.PayPalEndpoints.*;

@Component
@Slf4j
public class PayPalHttpClient {
    private final HttpClient httpClient;
    private final PayPalConfig payPalConfig;
    private final ObjectMapper objectMapper;

    @Autowired
    public PayPalHttpClient(PayPalConfig payPalConfig, ObjectMapper objectMapper) {
        this.payPalConfig = payPalConfig;
        this.objectMapper = objectMapper;
        httpClient = HttpClient.newBuilder().version(HttpClient.Version.HTTP_1_1).build();
    }

    public AccessTokenResponseDTO getAccessToken() throws Exception {
        var request = HttpRequest.newBuilder()
                .uri(URI.create(createUrl(payPalConfig.getBaseUrl(), GET_ACCESS_TOKEN)))
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, encodeBasicCredentials())
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en_US")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials"))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();
        return objectMapper.readValue(content, AccessTokenResponseDTO.class);
    }

    public ClientTokenDTO getClientToken() throws Exception {
        var accessTokenDto = getAccessToken();
        var request = HttpRequest.newBuilder()
                .uri(URI.create(createUrl(payPalConfig.getBaseUrl(), GET_CLIENT_TOKEN)))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenDto.getAccessToken())
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en_US")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();
        return objectMapper.readValue(content, ClientTokenDTO.class);
    }

    public OrderResponseDTO createOrder(OrderDTO orderDTO) throws Exception {
        var accessTokenDto = getAccessToken();
        var payload = objectMapper.writeValueAsString(orderDTO);

        var request = HttpRequest.newBuilder()
                .uri(URI.create(createUrl(payPalConfig.getBaseUrl(), ORDER_CHECKOUT)))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessTokenDto.getAccessToken())
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .build();
        var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        var content = response.body();
        return objectMapper.readValue(content, OrderResponseDTO.class);
    }

    private String encodeBasicCredentials() {
        var input = payPalConfig.getClientId() + ":" + payPalConfig.getSecret();
        return "Basic " + Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }
}
