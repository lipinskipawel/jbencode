package org.example;

import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.joining;

public final class Json {

    public static String stringToJson(String string) {
        return "\"%s\"".formatted(string).trim();
    }

    public static String listToJson(List<String> list) {
        final var collect = list.stream()
                .map(Json::replaceDoubleWithSingleQuote)
                .collect(joining(", "));
        return "[%s]".formatted(collect).trim();
    }

    public static String dictionaryToJson(Map<String, String> map) {
        final var collect = map
                .entrySet()
                .stream()
                .map(it -> it.getKey() + ": " + it.getValue())
                .map(Json::replaceDoubleWithSingleQuote)
                .collect(joining(", "));
        return "{%s}".formatted(collect);
    }

    private static String replaceDoubleWithSingleQuote(String string) {
        return string.replace("\"", "'");
    }
}
