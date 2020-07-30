package com.thoughtworks.security.oblivioustransfer;

import com.thoughtworks.security.oblivioustransfer.client.Quote;
import com.thoughtworks.security.oblivioustransfer.client.QuoteDTO;
import com.thoughtworks.security.oblivioustransfer.client.SellerClient;
import com.thoughtworks.security.oblivioustransfer.util.RSAUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static com.thoughtworks.security.oblivioustransfer.util.ByteUtil.CHOSEN_FACTOR_LENGTH;
import static com.thoughtworks.security.oblivioustransfer.util.ByteUtil.buildRandomMsg;
import static com.thoughtworks.security.oblivioustransfer.util.ByteUtil.decode;
import static com.thoughtworks.security.oblivioustransfer.util.ByteUtil.encode;
import static com.thoughtworks.security.oblivioustransfer.util.ByteUtil.xor;

@Service
@RequiredArgsConstructor
public class BuyerService {
    private final SellerClient sellerClient;

    public Quote getPrice(String id) throws BadPaddingException, InvalidKeyException, IllegalBlockSizeException {
        List<QuoteDTO> quoteDTOS = sellerClient.getAll();
        String publicKey = sellerClient.getPublicKey();

        QuoteDTO selectedQuoteDTO = quoteDTOS.stream().filter(quoteDTO -> quoteDTO.getSeller().equals("user" + id)).findFirst().get();
        byte[] chosenFactor = new byte[CHOSEN_FACTOR_LENGTH];
        new Random().nextBytes(chosenFactor);
        System.out.println("Generate factor for seller user" + id + ": " + encode(chosenFactor));
        String encryptedFactor = RSAUtil.obliviousTransferEncrypt(chosenFactor, getPublicKey(publicKey), buildRandomMsg(decode(selectedQuoteDTO.getRandomMsg())));
        System.out.println("Search with encrypted factor: " + encryptedFactor);
        List<QuoteDTO> quoteDTOAfterEncryption = sellerClient.search(encryptedFactor);
        List<Quote> quotes = quoteDTOAfterEncryption.stream().map(quoteDTO -> {
            byte[] encryptedPrice = decode(quoteDTO.getPriceAfterEncryption());
            byte[] priceBytes = xor(chosenFactor, encryptedPrice);
            int price = ByteBuffer.wrap(priceBytes).order(ByteOrder.nativeOrder()).getInt();
            System.out.println("seller: " + quoteDTO.getSeller() + ", price: " + price);
            return Quote.builder().seller(quoteDTO.getSeller()).price(price).build();
        }).collect(Collectors.toList());
        return quotes.stream().filter(quote -> quote.getSeller().equals("user" + id)).findFirst().get();
    }

    public PublicKey getPublicKey(String publicKey) {
        try {
            X509EncodedKeySpec X509publicKey = new X509EncodedKeySpec(decode(publicKey));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            return kf.generatePublic(X509publicKey);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}
