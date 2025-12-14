package com.ezh.Inventory.utils.common;

import java.security.SecureRandom;

public final class RandomCodeUtil {

    private static final String CHARSET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private RandomCodeUtil() {}

    //Generates XXXX (collision probability VERY LOW)
    public static String randomCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARSET.charAt(RANDOM.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }
}