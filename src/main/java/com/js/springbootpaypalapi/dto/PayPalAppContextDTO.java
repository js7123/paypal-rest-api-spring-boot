package com.js.springbootpaypalapi.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PayPalAppContextDTO {

    @JsonProperty("brand_name")
    private String brandName;

    @JsonProperty("landing_page")
    private PaymentLandingPageEnum landingPage;

    @JsonProperty("return_url")
    private String returnURL;

    @JsonProperty("cancel_url")
    private String cancelUrl;
}
