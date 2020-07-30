package com.thoughtworks.security.oblivioustransfer.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "OTServer", url = "http://localhost:8080")
public interface SellerClient {
    @GetMapping("/quotes")
    List<QuoteDTO> getAll();

    @GetMapping("/publicKey")
    String getPublicKey();

    @GetMapping("/quotesWithPrice")
    List<QuoteDTO> search(@RequestParam String encryptedFactor);
}
