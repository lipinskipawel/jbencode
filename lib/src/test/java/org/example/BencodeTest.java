package org.example;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.example.Bencode.decode;
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
        final var result = decode(bencode);

        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> integers() {
        return Stream.of(
                arguments("i3e", "3"),
                arguments("i-3e", "-3"),
                arguments("i0e", "0")
        );
    }

    @ParameterizedTest
    @MethodSource("integers")
    void decode_integers(String bencode, String expected) {
        final var result = decode(bencode);

        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> illegalIntegers() {
        return Stream.of(
                arguments("i03e", "03"),
                arguments("i-03e", "-03"),
                arguments("i-003e", "-003"),
                arguments("i00038e", "00038")
        );
    }

    @ParameterizedTest
    @MethodSource("illegalIntegers")
    void throw_when_illegally_encoded_integer(String bencode, String msg) {
        final var result = Assertions.catchThrowable(() -> decode(bencode));

        assertThat(result)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bencode protocol format does not allows leading zeros like [%s]".formatted(msg));
    }
}
