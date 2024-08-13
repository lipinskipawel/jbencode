package org.example;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.util.Objects.requireNonNull;
import static org.example.Json.listToJson;
import static org.example.Json.stringToJson;

public final class Bencode {
    private record Result<T>(T parsed, String bencodeLeft) {
        Result {
            requireNonNull(parsed);
            requireNonNull(bencodeLeft);
        }
    }

    public static String decode(String bencode) {
        return step(bencode).parsed();
    }

    private static Result<String> step(String bencode) {
        final var type = bencode.charAt(0);
        return switch (type) {
            case 'i' -> {
                final var integer = parseInteger(bencode);
                yield new Result<>(stringToJson(integer), bencode.substring(bencode.indexOf("e") + 1));
            }
            case 'l' -> {
                final var result = parseList(bencode);
                yield new Result<>(listToJson(result.parsed()), result.bencodeLeft());
            }
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
                final var splitString = bencode.split(":", 2);
                final var length = parseInt(splitString[0]);
                final var string = splitString[1].substring(0, length);
                yield new Result<>(stringToJson(string), splitString[1].substring(length));
            }
            default -> throw new IllegalArgumentException("Unknown character [%s]".formatted(type));
        };
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

    private static Result<List<String>> parseList(String bencode) {
        final var list = new ArrayList<String>();

        var item = step(bencode.substring(1));
        list.add(item.parsed());
        if (item.bencodeLeft.charAt(0) == 'e') {
            return new Result<>(list, item.bencodeLeft());
        }

        do {
            item = step(item.bencodeLeft());
            list.add(item.parsed());
        } while (item.bencodeLeft.charAt(0) != 'e');
        return new Result<>(list, item.bencodeLeft().substring(1));
    }
}
