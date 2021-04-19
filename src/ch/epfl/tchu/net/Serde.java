package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface Serde<T> {

    static <T> Serde<T> of(Function<T, String> serializer, Function<String, T> deserializer) {
        return new Serde<>() {
            @Override
            public String serialize(T raw) {
                return raw != null ? serializer.apply(raw) : "";
            }

            @Override
            public T deserialize(String serialized) {
                return ! serialized.isEmpty()
                        ? deserializer.apply(serialized)
                        : null;
            }
        };
    }

    static <T> Serde<T> oneOf(List<T> elements) {
        return Serde.of(
                t -> String.valueOf(elements.indexOf(t)),
                s -> elements.get(Integer.parseInt(s))
        );
    }

    static <T> Serde<List<T>> listOf(Serde<T> serde, String separator) {
        return new Serde<>() {
            @Override
            public String serialize(List<T> raw) {
                return raw.stream()
                        .map(serde::serialize)
                        .collect(Collectors.joining(separator));
            }

            @Override
            public List<T> deserialize(String serialized) {
                return Arrays.stream(serialized.split(Pattern.quote(separator), -1))
                        .map(serde::deserialize)
                        .collect(Collectors.toUnmodifiableList());
            }
        };
    }

    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separator) {
        Serde<List<T>> listSerde = listOf(serde, separator);
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> raw) {
                return listSerde.serialize(raw.toList());
            }

            @Override
            public SortedBag<T> deserialize(String serialized) {
                return SortedBag.of(listSerde.deserialize(serialized));
            }
        };
    }
    
    String serialize(T raw);
    T deserialize(String serialized);

}
