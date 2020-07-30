package com.thoughtworks.security.oblivioustransfer.util;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

import static com.thoughtworks.security.oblivioustransfer.util.ByteUtil.CHOSEN_FACTOR_LENGTH;
import static com.thoughtworks.security.oblivioustransfer.util.ByteUtil.XOR_RANDOM_NUMBER_LENGTH;
import static com.thoughtworks.security.oblivioustransfer.util.ByteUtil.xor;

@Slf4j
public class RSAUtil {

    private static KeyPairGenerator kpg;
    private static Cipher c;

    static {
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
            c = Cipher.getInstance("RSA/ECB/NoPadding");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static KeyPair genKeyPair() {
        return kpg.genKeyPair();
    }

    public static String encrypt(byte[] plaintext, PublicKey publicKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        c.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = c.doFinal(plaintext);
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static byte[] decrypt(String ciphertext, PrivateKey privateKey) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        c.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytes = Base64.getDecoder().decode(ciphertext);
        byte[] plaintext = c.doFinal(encryptedBytes);
        return Arrays.copyOfRange(plaintext, CHOSEN_FACTOR_LENGTH, plaintext.length);
    }


    public static String obliviousTransferEncrypt(byte[] chosenFactor, PublicKey publicKey, byte[] randomMsg) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        assert randomMsg.length == XOR_RANDOM_NUMBER_LENGTH;
        c.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] encryptedBytes = c.doFinal(chosenFactor);
        byte[] encryptedBytesWithRandom = xor(encryptedBytes, randomMsg);
        return Base64.getEncoder().encodeToString(encryptedBytesWithRandom);
    }

    public static byte[] obliviousTransferDecrypt(String encryptedFactor, PrivateKey privateKey, byte[] randomMsg) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        assert randomMsg.length == XOR_RANDOM_NUMBER_LENGTH;
        c.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] encryptedBytesWithRandom = Base64.getDecoder().decode(encryptedFactor);
        byte[] encryptedBytes = xor(encryptedBytesWithRandom, randomMsg);
        byte[] chosenFactor;
        try {
            chosenFactor = c.doFinal(encryptedBytes);
        } catch (BadPaddingException e) {
            // sometimes, there is a padding issue, to fix
            log.info(e.getLocalizedMessage());
            return new byte[CHOSEN_FACTOR_LENGTH];
        }
        return Arrays.copyOfRange(chosenFactor, CHOSEN_FACTOR_LENGTH, chosenFactor.length);
    }


}
