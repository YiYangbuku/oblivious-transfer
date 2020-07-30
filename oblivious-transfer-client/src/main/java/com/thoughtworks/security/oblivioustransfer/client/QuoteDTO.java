package com.thoughtworks.security.oblivioustransfer.client;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class QuoteDTO {
    private String seller;  // who give this prive
    private String randomMsg;   // random messages: x_{0},x_{1}
    private String priceAfterEncryption;
}
