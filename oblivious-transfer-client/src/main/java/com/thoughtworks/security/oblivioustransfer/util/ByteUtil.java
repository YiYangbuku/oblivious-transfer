package com.thoughtworks.security.oblivioustransfer.util;

import java.util.Base64;
import java.util.Random;

public class ByteUtil {

    public static final int CHOSEN_FACTOR_LENGTH = 128;
    public static final int XOR_RANDOM_NUMBER_LENGTH = 256;
    public static final int RANDOM_MSG_LENGTH = 64;

    public static String encode(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] decode(String str) {
        return Base64.getDecoder().decode(str);
    }

    public static byte[] xor(byte[] x1, byte[] x2)
    {
        byte[] out = new byte[x1.length];

        for (int i = x1.length - 1; i >= 0; i--)
        {
            out[i] = (byte)(x1[i] ^ x2[i]);
        }
        return out;
    }

    public static byte[] getRandomBytes() {
        byte[] randomMsg = new byte[RANDOM_MSG_LENGTH];
        new Random().nextBytes(randomMsg);
        return randomMsg;
    }

    public static byte[] buildRandomMsg(byte[] random) {
        assert random.length == RANDOM_MSG_LENGTH;
        byte[] randomMsg1 = new byte[XOR_RANDOM_NUMBER_LENGTH - RANDOM_MSG_LENGTH];
        byte[] randomMsg = new byte[XOR_RANDOM_NUMBER_LENGTH];
        System.arraycopy(randomMsg1, 0, randomMsg, 0, randomMsg1.length);
        System.arraycopy(random, 0, randomMsg, randomMsg1.length, random.length);
        return randomMsg;
    }

}
