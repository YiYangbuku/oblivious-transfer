package com.thoughtworks.security.oblivioustransfer;

import com.thoughtworks.security.oblivioustransfer.client.Quote;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;

@RestController
@RequestMapping("/quotes")
@RequiredArgsConstructor
public class BuyerController {

    private final BuyerService buyerService;

    @GetMapping("/{id}")
    public Quote getPrice(@PathVariable String id) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        return buyerService.getPrice(id);
    }
}
