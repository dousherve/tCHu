package ch.epfl.tchu.net;

import ch.epfl.tchu.SortedBag;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public interface Serde<T> {
    
    // MARK:- Séparateurs
    
    String COMMA = ",";
    String SEMI_COLON = ";";
    String COLON = ":";
    
    // MARK:- Méthodes statiques de découpage de String
    
    static String[] split(String toSplit, String separator) {
        return toSplit.split(Pattern.quote(separator), -1);
    }
    
    static String[] split(String toSplit) {
        return split(toSplit, SEMI_COLON);
    }
    
    // MARK:- Méthodes statiques de construction de serdes

    static <T> Serde<T> of(Function<T, String> serializer, Function<String, T> deserializer) {
        return new Serde<>() {
            @Override
            public String serialize(T raw) {
                return raw != null
                        ? serializer.apply(raw)
                        : "";
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
                raw -> String.valueOf(elements.indexOf(raw)),
                s -> elements.get(Integer.parseInt(s))
        );
    }

    static <T> Serde<List<T>> listOf(Serde<T> serde, String separator) {
        return Serde.of(
                raw -> raw.stream()
                        .map(serde::serialize)
                        .collect(Collectors.joining(separator)),
                
                s -> Arrays.stream(split(s, separator))
                        .map(serde::deserialize)
                        .collect(Collectors.toUnmodifiableList())
        );
    }
    
    static <T> Serde<List<T>> listOf(Serde<T> serde) {
        return listOf(serde, COMMA);
    }

    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde, String separator) {
        final Serde<List<T>> listSerde = listOf(serde, separator);
        return Serde.of(
                raw -> listSerde.serialize(raw.toList()),
                s -> SortedBag.of(listSerde.deserialize(s))
        );
    }
    
    static <T extends Comparable<T>> Serde<SortedBag<T>> bagOf(Serde<T> serde) {
        return bagOf(serde, COMMA);
    }
    
    // MARK:- Méthodes de Serde
    
    String serialize(T raw);
    T deserialize(String serialized);

}
