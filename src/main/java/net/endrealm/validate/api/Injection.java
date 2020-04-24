package net.endrealm.validate.api;

/**
 *
 */
@FunctionalInterface
public interface Injection<T> {
    T get(Class<?> tClass);
}
