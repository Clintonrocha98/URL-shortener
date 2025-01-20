package com.urlshorter.utils;

import java.util.Random;

public class KeyGenerator {

  private static final String VALID_CHARS = "0123456789abcdefghijklmnopqrstuvwxyzQWERTYUIOPASDFGHJKLZXCVBNM";
  private static final int KEY_LENGTH = 10;

  public static String generate() {
    StringBuilder result = new StringBuilder();
    Random rand = new Random();

    for (int i = 0; i < KEY_LENGTH; i++) {
      result.append(VALID_CHARS.charAt(rand.nextInt(VALID_CHARS.length())));
    }

    return result.toString();
  }
}