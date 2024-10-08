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
                arguments("4:spam", "\"spam\""),
                arguments("5:hello", "\"hello\""),
                arguments("10:reqeb[jiow", "\"reqeb[jiow\""),
                arguments("3:w w", "\"w w\"")
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
                arguments("i3e", "\"3\""),
                arguments("i-3e", "\"-3\""),
                arguments("i0e", "\"0\"")
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

    private static Stream<Arguments> lists() {
        return Stream.of(
                arguments("l4:spam4:eggse", "['spam', 'eggs']"),
                arguments("li3e2:eee", "['3', 'ee']"),
                arguments("lli2e4:abcdee", "[['2', 'abcd']]"),
                arguments("lli2e4:abcdei3333e9:iyrrdserre", "[['2', 'abcd'], '3333', 'iyrrdserr']"),
                arguments("lli2e4:abcdei3333e9:iyrrdserrli1e3:hjkee", "[['2', 'abcd'], '3333', 'iyrrdserr', ['1', 'hjk']]"),
                arguments("llli3e4:someeee", "[[['3', 'some']]]"),
                arguments("li34ee", "['34']")
        );
    }

    @ParameterizedTest
    @MethodSource("lists")
    void decode_lists(String bencode, String expected) {
        final var result = decode(bencode);

        assertThat(result).isEqualTo(expected);
    }

    private static Stream<Arguments> dictionary() {
        return Stream.of(
                arguments("d3:cow3:moo4:spam4:eggse", "{'cow': 'moo', 'spam': 'eggs'}"),
                arguments("d3:abcli33e1:aee", "{'abc': ['33', 'a']}"),
                arguments("d3:jjjlli2e4:abcdei3333e9:iyrrdserrli1e3:hjkeee", "{'jjj': [['2', 'abcd'], '3333', 'iyrrdserr', ['1', 'hjk']]}")
        );
    }

    @ParameterizedTest
    @MethodSource("dictionary")
    void decode_dictionary(String bencode, String expected) {
        final var result = decode(bencode);

        assertThat(result).isEqualTo(expected);
    }
}
