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
            public String serialize(T toSerialize) {
                return serializer.apply(toSerialize);
            }

            @Override
            public T deserialize(String toDeserialize) {
                return deserializer.apply(toDeserialize);
            }
        };
    }

    static <T> Serde<T> oneOf(List<T> elements) {
        return Serde.of(t -> String.valueOf(elements.indexOf(t)), s -> elements.get(Integer.parseInt(s)));
    }

    static <T> Serde<List<T>> listOf(Serde<T> serde, String separator) {
        return new Serde<>() {
            public String serialize(List<T> toSerialize) {
                return toSerialize.stream()
                        .map(serde::serialize)
                        .collect(Collectors.joining(separator));
            }

            @Override
            public List<T> deserialize(String toDeserialize) {
                return Arrays.stream(toDeserialize.split(Pattern.quote(separator), -1))
                        .map(serde::deserialize)
                        .collect(Collectors.toList());
            }
        };
    }

    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separator) {
        Serde<List<T>> listSerde = listOf(serde, separator);
        return new Serde<>() {
            @Override
            public String serialize(SortedBag<T> toSerialize) {
                return listSerde.serialize(toSerialize.toList());
            }

            @Override
            public SortedBag<T> deserialize(String toDeserialize) {
                return SortedBag.of(listSerde.deserialize(toDeserialize));
            }
        };
    }

    static <T> Serde<T> compositeOf(String separator, Serde... serdes) {
        return null;
    }


    String serialize(T toSerialize);
    T deserialize(String toDeserialize);

}
