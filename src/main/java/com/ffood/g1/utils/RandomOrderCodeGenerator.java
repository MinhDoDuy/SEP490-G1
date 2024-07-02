package com.ffood.g1.utils;

import java.security.SecureRandom;
import java.util.Random;

public class RandomOrderCodeGenerator {
    private static final String UPPERCASE_LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final int LETTER_COUNT = 5;
    private static final int NUMBER_COUNT = 12;
    private static final Random RANDOM = new SecureRandom();

    public static String generateOrderCode() {
        StringBuilder orderCode = new StringBuilder(LETTER_COUNT + NUMBER_COUNT);

        // Append random uppercase letters
        for (int i = 0; i < LETTER_COUNT; i++) {
            int index = RANDOM.nextInt(UPPERCASE_LETTERS.length());
            orderCode.append(UPPERCASE_LETTERS.charAt(index));
        }

        // Append random numbers
        for (int i = 0; i < NUMBER_COUNT; i++) {
            int index = RANDOM.nextInt(NUMBERS.length());
            orderCode.append(NUMBERS.charAt(index));
        }

        return orderCode.toString();
    }
}
