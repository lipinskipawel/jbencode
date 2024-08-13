package org.example;

import static org.example.Json.stringToJson;

public final class Bencode {

    public static String decode(String bencode) {
        final var type = bencode.charAt(0);
        if (type == 'i') {
            return stringToJson(parseInteger(bencode));
        }
        final var splitString = bencode.split(":", 2);
        return stringToJson(splitString[1]);
    }

    private static String parseInteger(String bencode) {
        final var number = bencode.substring(1, bencode.indexOf("e"));
        if (number.charAt(0) == '0' && number.length() >= 2) {
            throw new IllegalArgumentException("Bencode protocol format does not allows leading zeros like [%s]".formatted(number));
        }
        if (number.charAt(0) == '-' && number.charAt(1) == '0' && number.length() >= 3) {
            throw new IllegalArgumentException("Bencode protocol format does not allows leading zeros like [%s]".formatted(number));
        }
        return number;
    }
}
