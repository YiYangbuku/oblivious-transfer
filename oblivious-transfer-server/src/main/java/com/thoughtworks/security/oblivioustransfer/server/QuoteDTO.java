package com.thoughtworks.security.oblivioustransfer.server;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class QuoteDTO {
    private String seller;
    private String randomMsg;
    private String priceAfterEncryption;

    public static QuoteDTO from(Quote quote) {
        return QuoteDTO.builder().seller(quote.getSeller()).randomMsg(quote.getRandomMsg()).build();
    }
}
