package net.endrealm.validate.utils;

import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.function.Function;

/**
 *
 */
@Data
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Pair<K, V> {
    private final K key;
    private final V value;

    public static <T, K,V> Pair<K, V> from(T element, Function<T,K> getKey, Function<T,V> getValue) {
        return new Pair<>(getKey.apply(element), getValue.apply(element));
    }

    public static <K,V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }
}
