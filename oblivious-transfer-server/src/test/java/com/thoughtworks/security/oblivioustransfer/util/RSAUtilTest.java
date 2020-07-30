package com.thoughtworks.security.oblivioustransfer.util;

import org.junit.jupiter.api.Test;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.util.Random;

import static com.thoughtworks.security.oblivioustransfer.util.RSAUtil.CHOSEN_FACTOR_LENGTH;
import static com.thoughtworks.security.oblivioustransfer.util.RSAUtil.buildRandomMsg;
import static com.thoughtworks.security.oblivioustransfer.util.RSAUtil.getRandomBytes;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class RSAUtilTest {

    @Test
    void genKeyPair() {
        KeyPair keyPair = RSAUtil.genKeyPair();
        System.out.println(keyPair);
    }

    @Test
    void encryptAndDecrypt() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        KeyPair keyPair = RSAUtil.genKeyPair();
        byte[] msg = new byte[CHOSEN_FACTOR_LENGTH];
        new Random().nextBytes(msg);
        String encryptedMsg = RSAUtil.encrypt(msg, keyPair.getPublic());
        byte[] decryptedMsg = RSAUtil.decrypt(encryptedMsg, keyPair.getPrivate());
        assertArrayEquals(msg, decryptedMsg);
    }

    @Test
    void encryptWithRandomAndDecrypt() throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        KeyPair keyPair = RSAUtil.genKeyPair();
        byte[] chosenFactor = new byte[CHOSEN_FACTOR_LENGTH];
        new Random().nextBytes(chosenFactor);

        byte[] randomMsg = buildRandomMsg(getRandomBytes());
        String encryptedMsg = RSAUtil.obliviousTransferEncrypt(chosenFactor, keyPair.getPublic(), randomMsg);
        byte[] decryptedMsg = RSAUtil.obliviousTransferDecrypt(encryptedMsg, keyPair.getPrivate(), randomMsg);
        assertArrayEquals(chosenFactor, decryptedMsg);
    }

    @Test
    void encryptWithRandomAndDecryptWithOtherKey() {
        for (int i = 0; i < 100; i++) {
            assertDoesNotThrow(() -> {
                KeyPair keyPair = RSAUtil.genKeyPair();
                KeyPair anotherKeyPair = RSAUtil.genKeyPair();
                byte[] chosenFactor = new byte[CHOSEN_FACTOR_LENGTH];
                new Random().nextBytes(chosenFactor);

                byte[] randomMsg1 = buildRandomMsg(getRandomBytes());
                byte[] randomMsg2 = buildRandomMsg(getRandomBytes());

                String encryptedMsg = RSAUtil.obliviousTransferEncrypt(chosenFactor, keyPair.getPublic(), randomMsg1);
                RSAUtil.obliviousTransferDecrypt(encryptedMsg, anotherKeyPair.getPrivate(), randomMsg2);
            });
        }
    }

}