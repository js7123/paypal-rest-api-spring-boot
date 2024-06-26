package com.js.springbootpaypalapi.config;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
@ConfigurationProperties(prefix = "paypal")
public class PayPalConfig {
    @NotEmpty
    private String baseUrl;

    @NotEmpty
    private String clientId;

    @NotEmpty
    private String secret;
}
