package org.example;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;
import static org.example.Json.dictionaryToJson;
import static org.example.Json.listToJson;
import static org.example.Json.stringToJson;

public final class Bencode {
    private record Result(String parsed, String bencodeLeft) {
        Result {
            requireNonNull(parsed);
            requireNonNull(bencodeLeft);
        }
    }

    public static String decode(String bencode) {
        return step(bencode).parsed();
    }

    private static Result step(String bencode) {
        final var type = bencode.charAt(0);
        return switch (type) {
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> parseString(bencode);
            case 'i' -> parseInteger(bencode);
            case 'l' -> parseList(bencode);
            case 'd' -> parseDictionary(bencode);
            default -> throw new IllegalArgumentException("Unknown character [%s]".formatted(type));
        };
    }

    private static Result parseString(String bencode) {
        final var splitString = bencode.split(":", 2);
        final var length = parseInt(splitString[0]);
        final var string = splitString[1].substring(0, length);
        return new Result(stringToJson(string), splitString[1].substring(length));
    }

    private static Result parseInteger(String bencode) {
        final var number = bencode.substring(1, bencode.indexOf("e"));
        if (number.charAt(0) == '0' && number.length() >= 2) {
            throw new IllegalArgumentException("Bencode protocol format does not allows leading zeros like [%s]".formatted(number));
        }
        if (number.charAt(0) == '-' && number.charAt(1) == '0' && number.length() >= 3) {
            throw new IllegalArgumentException("Bencode protocol format does not allows leading zeros like [%s]".formatted(number));
        }
        return new Result(stringToJson(number), bencode.substring(bencode.indexOf("e") + 1));
    }

    private static Result parseList(String bencode) {
        final var list = new ArrayList<String>();
        var encodedValue = bencode.substring(1);

        do {
            final var item = step(encodedValue);
            list.add(item.parsed());
            encodedValue = item.bencodeLeft();
        } while (!encodedValue.startsWith("e"));

        return new Result(listToJson(list), encodedValue.substring(1));
    }

    private static Result parseDictionary(String bencode) {
        final var map = new HashMap<String, String>();
        var encodedValue = bencode.substring(1);

        while (!encodedValue.startsWith("e")) {
            final var key = step(encodedValue);
            encodedValue = key.bencodeLeft();
            final var value = step(encodedValue);
            map.put(key.parsed(), value.parsed());
            encodedValue = value.bencodeLeft();
        }
        return new Result(dictionaryToJson(map), encodedValue.substring(1));
    }
}
