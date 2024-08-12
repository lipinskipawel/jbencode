package org.example;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

class BencodeTest implements WithAssertions {

    private static Stream<Arguments> strings() {
        return Stream.of(
                arguments("4:spam", "spam"),
                arguments("5:hello", "hello"),
                arguments("10:reqeb[jiow", "reqeb[jiow"),
                arguments("3:w w", "w w")
        );
    }

    @ParameterizedTest
    @MethodSource("strings")
    void decode_strings(String bencode, String expected) {
        final var result = Bencode.decode(bencode);

        assertThat(result).isEqualTo(expected);
    }
}
