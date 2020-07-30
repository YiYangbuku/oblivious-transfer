package com.thoughtworks.security.oblivioustransfer.server;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @GetMapping("/quotes")
    public List<QuoteDTO> getAll() {
        return quoteService.getAll();
    }

    @GetMapping("/publicKey")
    public String getPublicKey() {
        return quoteService.getPublicKey();
    }

    @GetMapping("/quotesWithPrice")
    public List<QuoteDTO> search(@RequestParam String encryptedFactor) {
        return quoteService.search(encryptedFactor);
    }

}
