package org.example;

public final class Json {

    public static String stringToJson(String string) {
        return """
                "%s"
                """.formatted(string).trim();
    }
}
