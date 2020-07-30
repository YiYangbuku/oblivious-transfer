package com.thoughtworks.security.oblivioustransfer.client;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Quote {
    private String seller;  // who give this prive
    private int price;
}
