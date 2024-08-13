package org.example;

import java.util.List;

import static java.util.stream.Collectors.joining;

public final class Json {

    public static String stringToJson(String string) {
        return """
                "%s"
                """.formatted(string).trim();
    }

    public static String listToJson(List<String> list) {
        final var collect = list.stream()
                .map(it -> it.replace("\"", "'"))
                .collect(joining(", "));
        return """
                [%s]
                """.formatted(collect).trim();
    }
}
