package com.thoughtworks.security.oblivioustransfer.server;

import com.thoughtworks.security.oblivioustransfer.util.ByteUtil;
import com.thoughtworks.security.oblivioustransfer.util.RSAUtil;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.thoughtworks.security.oblivioustransfer.util.ByteUtil.decode;
import static com.thoughtworks.security.oblivioustransfer.util.ByteUtil.encode;
import static com.thoughtworks.security.oblivioustransfer.util.ByteUtil.getRandomBytes;
import static java.util.stream.Collectors.toList;

@Service
public class QuoteService {
    private static final int AMOUNT = 30;
    private static List<Quote> quoteList = new ArrayList<>();
    private static final KeyPair keyPair = RSAUtil.genKeyPair();
    
    public QuoteService() {
        for (int i = 0; i < AMOUNT; i++) {
            Quote quote = Quote.builder().seller("user" + i).price(new Random().nextInt(1000)).randomMsg(encode(getRandomBytes())).build();
            quoteList.add(quote);
            System.out.println("Seller: " + quote.getSeller() + ", price: " + quote.getPrice());
        }
    }

    public List<QuoteDTO> getAll() {
        return quoteList.stream().map(QuoteDTO::from).collect(toList());
    }

    public String getPublicKey() {
        return encode(keyPair.getPublic().getEncoded());
    }

    public List<QuoteDTO> search(String encryptedFactor) {
        System.out.println("Receive encrypted factor: " + encryptedFactor);
        return quoteList.stream().map(quote -> {
            try {
                byte[] factor = RSAUtil.obliviousTransferDecrypt(encryptedFactor, keyPair.getPrivate(), ByteUtil.buildRandomMsg(decode(quote.getRandomMsg())));
                System.out.println("Factor for seller " + quote.getSeller() + ": " + encode(factor) );
                byte[] priceByte = ByteBuffer.allocate(factor.length).order(ByteOrder.nativeOrder()).putInt(quote.getPrice()).array();
                byte[] encryptedPrice = ByteUtil.xor(factor, priceByte);
                return QuoteDTO.builder().seller(quote.getSeller()).priceAfterEncryption(encode(encryptedPrice)).build();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            }
            return null;
        }).collect(toList());
    }
}
