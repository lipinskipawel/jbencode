package org.example;

public final class Bencode {

    public static String decode(String bencode) {
        final var splitString = bencode.split(":", 2);
        return splitString[1];
    }
}
