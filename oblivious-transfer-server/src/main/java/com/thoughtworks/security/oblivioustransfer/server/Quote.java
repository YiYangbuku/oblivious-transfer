package com.thoughtworks.security.oblivioustransfer.server;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Quote {
    private String seller;
    private int price;
    private String randomMsg;   // random messages: x_{0},x_{1}
}
